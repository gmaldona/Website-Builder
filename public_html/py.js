let path = "./json_data/a5_json.json"
    fetch(path)
        .then(results => results.json())
        .then(data => { 
            identifiers = ""
            console.log(data.identifiers)
            data.identifiers.forEach(id => identifiers = `${identifiers}${id}<br>`)
            console.log(identifiers)
            document.getElementById(`identifiers`).innerHTML = identifiers;
    });