'use strict';

let result = document.getElementById("result");

document.getElementById("submit").addEventListener("click", function(){
    executeSearch();
});

document.getElementById("search").addEventListener("keyup", function(e){
    e.preventDefault();
    if(e.keyCode === 13){
        executeSearch();
    }
});



function executeSearch(){
     let query = document.getElementById("search").value;

    let queryWords = query.split(/(\s+)/).filter(function(e) { return e.trim().length > 0; });

    for(let i = 0; i < queryWords.length; i++){
        let queryWord = queryWords[i];
        let copy = queryWord;
        for(let k = 0; k < queryWord.length; k++){
            let character = queryWord.charAt(k);
            if(character.match(/[^A-Za-z0-9]+/)){
                copy = copy.replace(character, "%"+character.charCodeAt(0).toString(16));
            }
        }

        queryWords[i] = copy;
    }

    let urlQuery = "";

    for(let i = 0; i < queryWords.length; i++){
        if(i != 0){
            urlQuery += "&";
        }
        urlQuery += "query=" + queryWords[i];

    }

    let http = new XMLHttpRequest();
    let url = "http://localhost:8080/search?" + urlQuery;

    http.open("GET", url);
    http.send();

    http.onreadystatechange=function(){
        if(this.readyState===4 && this.status===200){
            let resultTable = "<table> <tr><th>Page</th><th>Score</th> </tr>"

            let requestResult = JSON.parse(http.responseText);

            for(let i = 0; i < 5; i++){
                resultTable += "<tr> <td>" + Object.entries(requestResult)[i][1].page.replace(/_/g, " ") +"</td> <td>" + Object.entries(requestResult)[i][1].score + "</td>";
            }

            resultTable +="</table>";
            result.innerHTML = resultTable;
        }
    }
}