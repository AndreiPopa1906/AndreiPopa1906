<!DOCTYPE html>
<html>
<head>
    <title>Directory Report</title>
</head>
<body>
<h1>Directory Report</h1>
<ul>
    <#list files as file>
        <li><#if file.directory>[Directory] <#else>[File] </#if>${file.name}</li>
    </#list>
</ul>
</body>
</html>
