<%@ taglib uri="/struts-tags" prefix="s" %>
<html>
<head>
<title>Hello World</title>
<link type="text/css" rel="stylesheet" href="css/product.css"/>

</head>
<body>


<div id="title">
      <h1>Alpha Office Supply</h1>
    </div>

<div id="myProducts">
<s:iterator  value="list">
<div id="products">
  <img src="<s:property value="externalUrl"></s:property>" >
  <h4><s:property value="productName"/><br/></h4>
  <h4>Price: $<s:property value="listPrice"/><br/></h4>
</div>
</s:iterator>
</div>




</body>
</html>
