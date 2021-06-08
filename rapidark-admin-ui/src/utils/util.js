// 使用unicode 编码
export function encodeUnicode(str) {
  var res = [];
  for (var i = 0; i < str.length; i++) {
    res[i] = "&#" + str.charCodeAt(i).toString(10);
  }
  return res.join("");
}
//使用unicode 解码
export function decodeUnicode(str) {
  var res = "";
  var charArray = str.split("&#");
  for (var i = 0; i < charArray.length; i++) {
    if (charArray[i] != "")
      res += String.fromCharCode(charArray[i]);
  }
  return res;
}
//把HTML格式的字符串转义成实体格式字符串
export function escapeHTMLString (str) {
  str = str.replace(/</g,'&lt;');
  str = str.replace(/>/g,'&gt;');
  return str;
}
//把实体格式字符串转义成HTML格式的字符串
export function escapeStringHTML (str) {
  str = str.replace(/&lt;/g,'<');
  str = str.replace(/&gt;/g,'>');
  str = str.replace(/&quot;/g,'"');

  return str;
}
