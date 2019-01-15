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
            let resultTable = "<table> <tr><th>Page</th><th>Total score</th> <th>Word freq. score</th> <th>Document location score</th> <th>Word distance score</th> <th>Page rank score</th></tr>"

            let requestResult = JSON.parse(http.responseText);

            for(let i = 0; i < 5; i++){
                resultTable += "<tr>";
                resultTable += "<td>" + Object.entries(requestResult)[i][1].page.replace(/_/g, " ") +"</td>";
                resultTable += "<td>" + Object.entries(requestResult)[i][1].totalScore.toFixed(3) + "</td>";
                resultTable += "<td>" + Object.entries(requestResult)[i][1].wordFrequencyScore.toFixed(3) + "</td>";
                resultTable += "<td> " + Object.entries(requestResult)[i][1].documentLocationScore.toFixed(3) +"</td>";
                resultTable += "<td> " + Object.entries(requestResult)[i][1].wordDistanceScore.toFixed(3) +"</td>";
                resultTable += "<td> " + Object.entries(requestResult)[i][1].pageRankScore.toFixed(3) +"</td>";
                resultTable += "</tr>";

            }

            resultTable +="</table>";
            result.innerHTML = resultTable;
        }
    }
}