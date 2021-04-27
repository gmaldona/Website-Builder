# CSC 344 Website for Projects

This repository contains the front end and back end of the website. The python script reads the project's source code and gathers information. This information is stored in seperate JSON files. 


```JSON

{ "filename": "main.clj",
"lines": 214,
"identifiers" :["not","n","PersistentList","simplify","clojure","and","cond","the","demorgans","arg","into","exp","drop","new","seq","last","but","e",
"fn","first","one","boolean","apply","LazySeq","turn","m","or","substitute","c","symbol","type","pop","lang","contains","l","map","coll","nth",
"operation","lookup","i","conj","notexp","false","import","list","concat","second","evalexp","operator","is","if","count","ns","variable",
"main","get","andexp","Boolean","orexp","defn","If","to","true","two","vec","let"] }
```

This JSON data is displayed in the HTML files found in the repository. 

The python script also contains an email function where given an email, the script will use ```mutt``` to send a .tar.gz file of this repository. 

The website is also held on the Oswego Computer Science Department Servers www.cs.oswego.edu/~gmaldona/CSC344/.
