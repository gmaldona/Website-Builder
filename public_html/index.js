for(let i = 1; i <= 5; i++) {
    let path = `./json_data/a${i}_json.json`
    fetch(path)
        .then(results => results.json())
        .then(data => { 
            console.log(data.filename)
            document.getElementById(`project${i}_filename`).innerHTML    = data.filename;
            document.getElementById(`project${i}_lines`).innerHTML       = `${data.lines} lines`;
    });
}