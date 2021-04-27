#include <dirent.h>
#include <string.h>
#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/stat.h>
#include <stdbool.h>


struct node {
    char letter;
    struct node* children[127];
    struct node* parent;
    int numberOfChildren;
};

void sort(char** list, int numberOfFiles) {
    for (int i = 0; i < numberOfFiles; i++) {
        for (int j = i; j < numberOfFiles; j++) {
            if (i == j)
                continue;
            if (list[j][0] < list[i][0]) {
                char tempWord[strlen(list[i])];
                strcpy(tempWord, list[i]);
                strcpy(list[i], list[j]);
                strcpy(list[j], tempWord);
            }
            else if (list[j][0] == list[i][0]) {
                int shortestWordLength = 0;
                if (strlen(list[i]) <= strlen(list[j]))
                    shortestWordLength = strlen(list[i]);
                else
                    shortestWordLength = strlen(list[j]);

                for (int k = 0; k < shortestWordLength; k++) {
                    if (list[i][k] == list[j][k])
                        continue;
                    else if (list[j][k] < list[i][k]) {
                        char tempWord[strlen(list[i])];
                        strcpy(tempWord, list[i]);
                        strcpy(list[i], list[j]);
                        strcpy(list[j], tempWord);
                        break;
                    }
                }
            }
        }
    }
}

void addToTrie(char* name, struct node* node, unsigned int size) {
    if (name[0] == '\\') {
        return;
    }
    if (size > 0) {
        char newName[strlen(name) - 1];
        for (int i = 1; i < strlen(name); i++)
            newName[i - 1] = name[i];

        const char currentLetter = name[0];
        const int NUMBER_OF_CHILDREN = 127;
        const unsigned int CHILDREN_ALLOCATION_SIZE = NUMBER_OF_CHILDREN * sizeof(struct node *);

        if (node->children[0] == NULL || node->children[0]->letter == '\0') {
            struct node* childPtr = (struct node*) malloc(sizeof(struct node*));
            childPtr->letter = currentLetter;
            *childPtr->children = (struct node*) malloc(sizeof(CHILDREN_ALLOCATION_SIZE));
            childPtr->parent = node;
            node->children[0] = childPtr;
            node->children[1] = NULL;
            node->numberOfChildren ++;
            if (size > 1) {
                addToTrie(newName, childPtr, size - 1);
                return;
            }

        }

        else {
            for (int i = 0; i < node->numberOfChildren + 1; i++) {

                if (node->children[i] == NULL) {
                    struct node *childPtr = (struct node*) malloc(sizeof(struct node*));
                    *childPtr->children = (struct node*) malloc(sizeof(CHILDREN_ALLOCATION_SIZE));
                    childPtr->letter = currentLetter;
                    childPtr->parent = node;
                    node->children[i] = childPtr;
                    if (i < NUMBER_OF_CHILDREN - 1)
                        node->children[i+1] = NULL;
                    node->numberOfChildren ++;
                    if (size > 1) {
                        addToTrie(newName, childPtr, size - 1);
                        return;
                    }
                }

                struct node* child = node->children[i];
                if (child != NULL) {
                    if (node->children[i]->letter == currentLetter) {
                        if (size > 1) {
                            addToTrie(newName, node->children[i], size - 1);
                            return;
                        }
                    }
                }
            }
            return;
        }
    }

}


void backTrackTrie(struct node* node) {

    if(node->letter == ' ') {

        printf(" ");
        return;
    }

    backTrackTrie(node->parent);


    printf("%c", node->letter);
}


void findTerminatorNodes(struct node* node) {

    if (node == NULL)
        return;

    if (node->letter == '\0') {
        backTrackTrie(node);
    }
    else {
        for (int i = 0; i < node->numberOfChildren+1; i++) {
            if (node->children[i] == NULL)
                break;
            if (node->children[i]->letter == '\0')
                node->children[i]->parent = node;
            findTerminatorNodes(node->children[i]);
        }
    }

}

void searchTrie(struct node* node, char* name, int size) {

    if (size > 0) {
        char newName[strlen(name) - 1];
        char letter = name[0];
        for (int i = 1; i < strlen(name); i++)
            newName[i - 1] = name[i];

        for (int i = 0; i < node->numberOfChildren; i++) {
            if (node->children[i]->letter == letter) {
                searchTrie(node->children[i], newName, size - 1);
                return;
            }
            if (i == node->numberOfChildren - 1) {
                printf("No files found\n");
            }
        }
    }

    else {
        findTerminatorNodes(node);
        printf("\n");
    }
}

int main() {

    int exit = 0;

    while (exit == 0) {

        char input[100];
        printf("Enter a directory: >");
        scanf("%s", input);

        if (strcmp(input, "exit") == 0) {
            exit = 1;
            continue;
        }

        DIR *d;
        struct dirent *dir;
        struct stat filestat;

        d = opendir(input);

        const int MAX_FILES = 100;
        int counter = 0;
        char* files[MAX_FILES];

        if (d) {
            while ((dir = readdir(d)) != NULL) {

                stat(dir->d_name, &filestat);
                if (S_ISDIR(filestat.st_mode) || dir->d_name[0] == '.' || strchr(dir->d_name, 32) != NULL)
                    continue;
                else
                    files[counter] = dir->d_name;

                counter++;
            }
            closedir(d);
        }

        sort(files, counter);

        const int CHILDREN_ALLOCATION_SIZE = 127 * sizeof(struct node *);
        struct node head = {' ', malloc(CHILDREN_ALLOCATION_SIZE)};
        struct node *headPtr = &head;

        for (int i = 0; i < counter; i++) {
            char *file = files[i];
            if (file != NULL)
                addToTrie(file, headPtr, strlen(file));
            else
                break;
        }

        int cd = 0;

        while (cd == 0) {
            char prefix[100];
            printf(">");
            scanf("%s", prefix);

            if (strcmp(prefix, "cd") == 0)
                cd = 1;
            else if (strcmp(prefix, "ls") == 0) {
                for (int i = 0; i < counter; i++)
                    printf("%s\n", files[i]);
            }
            else
                searchTrie(headPtr, prefix, strlen(prefix));
        }
    }
    printf("...\n");

    return 0;
}
