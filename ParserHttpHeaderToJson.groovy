import groovy.json.JsonOutput

// the separator used for splitting the csv columns
String separator = ","
def dataList = []
def lineCounter = 0
def headers = []

def string = "HTTP/1.1 201 Created\n" +
        "Cache-Control: no-cache\n" +
        "Pragma: no-cache\n" +
        "Content-Type: text/csv\n" +
        "Expires: -1\n" +
        "Server: Microsoft-IIS/8.5\n" +
        "Content-Disposition: attachment; filename=\"Audit Log 2016-02-24.csv\"\n" +
        "X-Powered-By: ASP.NET\n" +
        "Date: Fri, 26 Feb 2016 10:34:35 GMT\n" +
        "Set-Cookie: site=b;expires=Fri, 26-Feb-2016 12:39:35 GMT;path=/;domain=ediscoverypoint.ci.thomsonreuters.com;HttpOnly;\n" +
        "Set-Cookie: ig=cishared_b_1;path=/;domain=ediscoverypoint.ci.thomsonreuters.com;HttpOnly;\n" +
        "Set-Cookie: emr_pm=!IZv8ob/cvk7T6+gQBjW+y0P+EOK/zZNJ0lVIjeePhsh/48Pr4knCmZ+iRiKdNRFWE7LusebolKk=;path=/;domain=ediscoverypoint.ci.thomsonreuters.com;HttpOnly;\n" +
        "Content-Encoding: gzip\n" +
        "Content-Length: 303\n" +
        "Connection: Keep-Alive\n" +
        "\n" +
        "Audit Log for AM_CI_RegressionTesting_2816\n" +
        "Dates: 02/24/2016 - 02/26/2016\n" +
        "Selected participants: Andrei Mitrofanov\n" +
        "Number of events: 2\n" +
        "\n" +
        "User name,Date & Time,Doc Number,Name,Type,Activity,Details\n" +
        "Andrei Mitrofanov,2/24/2016 8:17:01 AM,134,File3243.txt,Review,A user adds a note to a document,Note text: noteText\n" +
        "Andrei Mitrofanov,2/24/2016 9:09:50 AM,,,Access,User logs in to Cayman,Browser: Chrome"


def strings = string.split("\n")
def startData = strings.findLastIndexOf {it == ""}
def csvData = Arrays.copyOfRange(strings,startData + 1,strings.length)

csvData.each { line ->
        if (lineCounter == 0) {
            headers = line.split(separator).collect{it.trim()}.collect{it.toLowerCase()}
        } else {
            def dataItem = [:]
            def row = line.split(separator).collect{it.trim()}.collect{it.toLowerCase()}

            headers.eachWithIndex() { header, index ->
                dataItem.put("${header}", "${row[index]}")
            }
            dataList.add(dataItem)
        }
    lineCounter = lineCounter + 1
}

String output = JsonOutput.toJson(dataList)

print output

def ooo =9;
