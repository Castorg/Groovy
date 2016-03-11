import java.io.*
import java.util.regex.*
import groovy.json.*

class ReportParser
{
    // all field public for analyse inner job this tool.
    def public OffsetEnd = 4    // raw response contains empty string symbol in the end, and we must cut this
    def public HeadersCount
    def public FieldsByStrings
    def public splitted = []
    def public csvArray = []
    def public compose = [][]
    def public DetailsJsonArray
    def public mapJson = []
    def public rawCsvDetails = []

    def private ParseCsvToValisStrings(response)
    {
        def csvData = response.split("\r")
        def csvString
        int index = csvData.findIndexOf(csvData.findIndexOf{it.size() == 1} + 1){ it.size() == 1}
        for(int i = index; i < csvData.length; i++)
        {
            csvString += csvData[i] + "\r"
        }

        def countComInRow =[]
        def jsonStrings = csvString.split("\r")

        for(int i = 1; i < jsonStrings.length; i++)
        {
            int count = 0
            Pattern p = Pattern.compile(",", Pattern.UNICODE_CASE|Pattern.CASE_INSENSITIVE)
            Matcher m = p.matcher(jsonStrings[i])
            while(m.find()) count++
            countComInRow[i] = count
        }

        FieldsByStrings = []
        int countRows =0;
        def comCount = countComInRow[1]
        HeadersCount = comCount + 1
        for(int i = 1; i < jsonStrings.length; i++)
        {
            if(countComInRow[i] < comCount)
            {
                FieldsByStrings[countRows - 1] += jsonStrings[i] + " "
            }
            else
            {
                FieldsByStrings[countRows] = jsonStrings[i]
                countRows = countRows + 1
            }
        }

        // Delete "new string" in the last string
        FieldsByStrings[FieldsByStrings.size( ) - 1] = FieldsByStrings.last()[0..FieldsByStrings.last().size() - OffsetEnd]

        return FieldsByStrings
    }

    def private CreateValidCsv(separetedStrings)
    {
        for(int i = 0; i < separetedStrings.size; i++)
        {
            splitted[i] = separetedStrings[i].split(",")
        }

        for(int i = 0; i < splitted.size; i++)
        {
            if(splitted[i].size() <= HeadersCount - 1)      //validate simple fields without DETAILS
            {
                for (int j = 0; j <= HeadersCount - 1; j++)
                {
                    if(j < splitted[i].size())
                    {
                        if(csvArray[i] == null)
                        {
                            csvArray[i] = '"' + splitted[i][j] + '"'
                        }
                        else
                        {
                            csvArray[i] += '"' + splitted[i][j] + '"'
                        }
                    }
                    else
                    {
                        csvArray[i] += '""'   // empty value
                    }

                    if(j != HeadersCount - 1)
                    {
                        csvArray[i] += ","
                    }
                }

            }
            else        //validate DETAILS fields
            {
                for (int j = 0; j < splitted[i].size(); j++)
                {
                    if(j < HeadersCount - 1)
                    {
                        if(csvArray[i] == null)
                        {
                            csvArray[i] = '"' + splitted[i][j] + '"'
                        }
                        else
                        {
                            csvArray[i] += '"' + splitted[i][j] + '"'
                        }

                    }
                    else
                    {
                        if(i != 0)   //body in details wrapper in source data
                        {
                            csvArray[i] += splitted[i][j]
                        }
                        else       //header must wrapped in quotes
                        {
                            csvArray[i] += '"' + splitted[i][j] + '"'
                        }

                    }
                    //CSV SEPARATE
                    if(j != HeadersCount - 1)
                    {
                        csvArray[i] += ","
                    }
                }
            }
        }

        for (int a =0; a < csvArray.size(); a++)
        {
            csvArray[a] = csvArray[a].replace("\n","\t")
        }

        return csvArray
    }

    def private ValidateCsvDatailsToJson(validCsv)
    {
        def jsonArray = []
        for (int i =0; i < validCsv.size(); i++)
        {
            if(i != 0)
            {
                def overCount = validCsv[i].split(",").size()
                if(overCount == HeadersCount)
                {
                    rawCsvDetails[i] = validCsv[i].split(",")[HeadersCount - 1].split("\t")
                }
                else
                {
                    rawCsvDetails[i] = validCsv[i].split(",")[HeadersCount - 1..overCount - 1].join(',').split("\t")
                }

                jsonArray[i] = ConvertDetails(rawCsvDetails[i])
            }
        }
        DetailsJsonArray = jsonArray

        return DetailsJsonArray
    }

    def  private ConvertDetails(arg)
    {
        if(arg[0] == '""')
            return '""""' //""  need double quotes for escape charactere


        def mode = 0    // 0 simple  1 modify  2 field 3 respons
        def fields = []
        String jsonstr = "";
        int posBefore = -1;
        int posAfter = -1;
        for (int i = 0; i < arg.size(); i++)
        {
            if(i ==0)
            {
                jsonstr = "{"
            }
            if(arg[i].contains('"Fields:'))
            {
                jsonstr += '"Fields":"{'
                mode = 2
            }

            if(arg[i].contains('"Responsive:'))
            {
                jsonstr += '"Responsive":"{'
                mode = 3
            }

            if(arg[i].contains("Before modification:") || arg[i].contains("After modification:"))
            {
                mode = true
                if(arg[i].contains("Before modification:"))
                {
                    if(posBefore == -1){
                        posBefore = i
                    }
                }
                if(arg[i].contains("After modification:"))
                {
                    if(posAfter == -1){
                        posAfter = i
                    }
                }
            }
            else
            {
                if(mode == 0)       // field and value separated by ':'
                {
                    fields[i] = '"' + arg[i].replace("\"", "").split(":")[0] + '"' + ":" + '"' + arg[i].replace("\"", "").split(":")[1] + '"'

                    if (i == (arg.size() - 1))
                    {
                        if (fields.size() == 1)
                        {
                            jsonstr += fields
                        }
                        else
                        {
                            jsonstr += fields.join(",")
                        }
                        jsonstr += "}"
                    }
                }
                if(mode == 1)   // field and value separated by '='
                {
                    fields[i] = '"' + arg[i].replace("\"", "").split("=")[0] + '"' + ":" + '"' + arg[i].replace("\"", "").split("=")[1] + '"'

                    if (i == (arg.size() - 1))
                    {
                        if (fields.size() == 1)
                        {
                            jsonstr += fields
                        }
                        else
                        {
                            if(posBefore == -1)
                            {
                                jsonstr += fields.join(",")
                            }
                            else
                            {
                                def inner = ""

                                for(int j = 0; j < fields.size; j++)
                                {
                                    if(j < posBefore)
                                    {
                                        inner += fields[j] + ","
                                    }
                                    if(j == posBefore)
                                    {
                                        inner += "\"Before modification\" : {"
                                    }
                                    if(j > posBefore && j < posAfter)
                                    {
                                        inner += fields[j] + ","
                                    }
                                    if(j == posAfter)
                                    {
                                        inner += "} ,\"After modification\" : {"
                                    }
                                    if(j > posAfter)
                                    {
                                        inner += fields[j] + ","
                                    }
                                }
                                jsonstr += inner
                            }
                        }
                        jsonstr += "}"
                    }
                }
                if(mode == 2)   // field and value separated by ':' but value is object
                {
                    if(arg[i] == '"Fields:') //Fields: NameVP4VSWyU5fLTkhuw06ac0BZfcEG3KghL = someValue
                    {
                        i++
                    }

                    fields[i] = '"' + arg[i].replace("\"", "").split("=")[0] + '"' + ":" + '"' + arg[i].replace("\"", "").split("=")[1] + '"'
                    if (i == (arg.size() - 1))
                    {
                        jsonstr += fields[1..fields.size() - 1].join(",")
                        jsonstr += "}}"
                    }
                }
                if(mode == 3)
                {
                    if(arg[i] == '"Responsive:')
                    {
                        i++
                    }

                    fields[i] = '"' + arg[i].replace("\"", "").split("=")[0] + '"' + ":" + '"' + arg[i].replace("\"", "").split("=")[1] + '"'
                    if (i == (arg.size() - 1))
                    {
                        jsonstr += fields[1..fields.size() - 1].join(",")
                        jsonstr += "}}"
                    }
                }
            }
        }
        return jsonstr
    }

    def private InsertDetailsInJson(csvArray)
    {

        def retCsvArray = []
        retCsvArray[0] = csvArray[0]
        for (int i =1; i < csvArray.size(); i++)
        {
            compose[i] = []
            for (int j =0; j < HeadersCount; j++)
            {
                if(j < HeadersCount - 1)  // rewrite simple fields
                {
                    compose[i][j] = this.splitted[i][j].replaceAll("\\\\", "")
                }
                else                    // rewrite details
                {
                    compose[i][j] = this.DetailsJsonArray[i].replaceAll("\\\\", "")
                }
            }
            retCsvArray[i] = compose[i].join(",")
        }

        return retCsvArray
    }


    def private CreateJson(csvArray2)
    {
        def lineCounter = 0
        def headers = []
        String separator = ","
        for (int a =0; a < csvArray2.size(); a++)
        {
            if (lineCounter == 0)
            {
                headers = csvArray[0].split(separator)
            }
            else {
                def dataItem = [:]
                def row = compose[a].collect{it.trim()}.collect{it.toLowerCase()}

                headers.eachWithIndex() { header, index1 ->
                    dataItem.put("${header}", "${row[index1]}")
                }
                mapJson.add(dataItem)
            }
            lineCounter = lineCounter + 1
        }

        return mapJson
    }

    def private RemoveMetaSymbols(input)
    {
        // REMOVE UNUSED METASYMBOLS
        def a1 = input.replaceAll('\"\\{', '{').replaceAll('\\}\"', '}')        // CHANGE \{  {
        def a2 =  a1.replaceAll('\\[','').replaceAll('\\]','')                  // DELETE \[  \]
        def a3 =  a2.replaceAll(":\"\"", ':""""')                               // CHANGE :""  :""""
        def a4 =  a3.replaceAll("\\\\\"", '"').replaceAll("\"\\\\", '"')        // CHANGE \{  {  AND }/ }
        def a5 =  a4.replaceAll('""', '"')                                      // CHANGE ""  "
        def a6 =  '[' + a5.replaceAll('"""', '""').replaceAll('\\{\"\\}', '""}').replaceAll("\\\\", ' ') + ']'
        def a7 =  a6.replaceAll("t u043F u00BB u0457","") // remove symbol 'red dot'. From this symbol beginning all strings

        return a7
    }

    def ParseCsvWithDetails(rawResponse)
    {
        def strs = this.ParseCsvToValisStrings(rawResponse)

        def valid = this.CreateValidCsv(strs)

        this.ValidateCsvDatailsToJson(valid)

        def validDetails = this.InsertDetailsInJson(valid)

        this.CreateJson(validDetails)

        String output = JsonOutput.toJson(this.mapJson)

        def result = this.RemoveMetaSymbols(output)

        return result
    }
}

def rawResponse = new File('C:/Users/Maksim_Pitkevich/Desktop/Andrey/AllEvents.txt').text
ReportParser a =  new ReportParser()
def jsonString = a.ParseCsvWithDetails(rawResponse)
def jsonMap = new JsonSlurper().parseText(jsonString)
def g = jsonMap['Details'][0]



