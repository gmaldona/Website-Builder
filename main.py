import json
from microproject import wc_dir
import os
import re

"""
json = {{
            \"{fn}\": [
                {{ \"Number Of Lines\" : {nl} }}}}, 
                {{ \"Identifiers\" : {id} }}
            ]
        }}
"""

DIR = "CSC344"
SUB_DIR = [
    "a1/",
    "a2/",
    "a3/",
    "a4/",
    "a5/"
    ]

def get_filename(dir: str) -> str:
    path = DIR + '/' + dir
    stream = os.popen("cd {}; ls".format(path))
    filename = stream.readlines()[0].strip()
    return filename

def get_file_data(dir: str) -> str:
    filename = get_filename(dir)
    path = DIR + '/' + dir + filename
    f_string = None
    with open(path, 'r') as f:
        f_string = f.read()
    return f_string

def remove_strings(dir: str) -> str:
    file = get_file_data(dir)
    while True:
        if file.find('"') != -1:
            first_index = file.find('"')
            file = file[:first_index] + file[(first_index + 1):]
            second_index = file.find('"')
            file = file[:second_index] + file[(second_index + 1):]
            file = file[:first_index] + file[(second_index + 1):]
        else:
            break
    return file

def get_identifiers(dir: str) -> str:
    file = remove_strings(dir)
    identifiers = re.findall('[A-Za-z]+', file)
    identifiers = list(set(identifiers))
    temp = '['
    for i in range(0, len(identifiers)):
        if i == 0:
            temp = temp + '"' + identifiers[i] + '"'
        else:
            temp = temp + ',' + '"' + identifiers[i] + '"'
    temp = temp + ']'
    identifiers = temp
    return identifiers       
        
def create_json(dir: str) -> str:
    filename = get_filename(dir)
    print(filename)
    number_of_lines = wc_dir(DIR + '/' + dir)
    identifiers = get_identifiers(dir)
    
    json = "{{ \"filename\": \"{fn}\", \"lines\": {nl}, \"identifiers\": {id} }}".format(fn=filename, nl=number_of_lines[filename], id=identifiers)
    print(json)
    json_file_name = "public_html/json_data" + '/' + dir[:2] + '_json.json'
    with open(json_file_name, 'w') as file:
        file.write(json)
        file.close()
                
if __name__ == '__main__':
    for sdir in SUB_DIR:
        create_json(sdir)
        

