#include <iostream>
#include <vector>
#include <random>
#include <windows.h>

using namespace std;

vector<vector<int>> generateQuadraticField(int size);
vector<int> getCellValue(string cell);
int getPoints(int removedElements);

void printField(vector<vector<int>> field);
void removeElements(vector<vector<int>> &field, vector<int> &cell, int &counter);
void shrinkVertical(vector<vector<int>> &field);
void shrinkHorizontal(vector<vector<int>> &field);

bool hasAdjacent(vector<vector<int>> &field, vector<int> &cell);
bool gameOver(vector<vector<int>> &field);


int main()
{
    // Farben der Steine: blau(1), grün(2), rot(3), gelb(4), weiß(5), leer(0)

    cout << "Das folgende Spiel ist nach dem Prinzip des SameGames aufgebaut. Eine Zugeingabe erfolgt \x81 \bber Zeilenzahl der Form a-i und Spaltenzahl der Form 1-9." << endl;
    cout << "Das Feld oben links hat so z.B. die Bezeichnung a1. Geben Sie bitte nur g\x81ltige Felder ein. "
            "Das Spiel endet automatisch sobald keine Z\x81ge mehr m\x94glich sind." << endl;

    vector<vector<int>> spielfeld = generateQuadraticField(9);
    printField(spielfeld);
    int removedElements = 0;
    int points = 0;

    while (true)
    {
        // Console wieder weiß färben damit normale Schrift nicht Bunt wird
        HANDLE h = GetStdHandle(STD_OUTPUT_HANDLE);
        SetConsoleTextAttribute(h, 15);
        cout << "Erreichte Punkte: " << points << endl;

        if (gameOver(spielfeld))
        {
            cout << "Keine Spielz\x81ge mehr m\x94glich." << endl;
            break;
        }

        string eingabe;
        vector<int> cell;

        // Wiederhole die Eingabeaufforderung, bis eine gültige Eingabe gemacht wurde
        while (true)
        {
            cout << "Geben Sie ein Feld der Form a1 ein: " << endl;
            cin >> eingabe;

            // Wandelt die Eingabe in passende Form um und prüft, ob sie tatsächlich ein Element ist
            cell = getCellValue(eingabe);

            // Überprüfe, ob die Eingabe gültig ist
            if (cell.empty())
            {
                continue; // Eingabe ist ungültig, fordere zur erneuten Eingabe auf
            }

            // Überprüfe, ob die Zellkoordinaten im Bereich des Spielfelds liegen
            if (cell[0] >= spielfeld.size() || cell[1] >= spielfeld[0].size())
            {
                continue; // Ungültige Eingabe, fordere zur erneuten Eingabe auf
            }

            // Wenn die Eingabe gültig ist, breche die Schleife und fahre fort
            break;
        }

        // Falls die Eingabe gleiche Nachbarn hat, wird die Gruppe entfernt
        if (hasAdjacent(spielfeld, cell))
        {
            removeElements(spielfeld, cell, removedElements);
            points += getPoints(removedElements);
            removedElements = 0;
        }
        else
        {
            cout << "Feld " << eingabe << " wurde nicht entfernt." << endl;
        }

        // Zeilen/Spalten rutschen ggf. auf
        shrinkVertical(spielfeld);
        shrinkHorizontal(spielfeld);
        printField(spielfeld);
    }
    return 0;
}

// generiere Spielfeld mit unterschiedlichen Werten der Steine
vector<vector<int>> generateQuadraticField(int size)
{
    vector<vector<int>> spielfeld;
    random_device rd;
    mt19937 gen(rd());
    uniform_int_distribution<> distr(1, 5);

    for (int i = 0; i < size; i++)
    {
        vector<int> spielfeldRow;
        for (int j = 0; j < size; j++)
        {
            int c = distr(gen);
            spielfeldRow.push_back(c);
        }
        spielfeld.push_back(spielfeldRow);
    }
    return spielfeld;
}

// Färbe Steine entsprechend ihres Wertes
void printField(vector<vector<int>> field)
{
    HANDLE h = GetStdHandle(STD_OUTPUT_HANDLE);
    for(int i = 0; i<field.size(); i++)
    {
        for(int j = 0; j<field[i].size(); j++)
        {
            switch (field[i][j])
            {
                case 0:
                    SetConsoleTextAttribute(h, 15);
                    cout <<  " " << " ";
                    break;
                case 1:
                    SetConsoleTextAttribute(h, 3);
                    cout << "O" << " ";
                    break;
                case 2:
                    SetConsoleTextAttribute(h,10);
                    cout << "O" << " ";
                    break;
                case 3:
                    SetConsoleTextAttribute(h,4);
                    cout << "O" << " ";
                    break;
                case 4:
                    SetConsoleTextAttribute(h,14);
                    cout << "O" << " ";
                    break;
                case 5:
                    SetConsoleTextAttribute(h,5);
                    cout << "O" << " ";
                    break;
            }
        }
        cout << endl;
    }
}

// Erhalte numerischen Wert der Eingabe nach ASCII-Tabelle, bei falscher Eingabe gib leeren Vektor zurück
vector<int> getCellValue(string cell)
{
    vector<int> v;
    //a-i; 1-9

    if (cell.length() != 2)
    {
        cout << "Geben Sie nur 2 Zeichen ein!" << endl;
        return v;
    }

    int part1 = (int(cell[0]));
    int part2 = (int(cell[1]));

    if (part1 < 97 || part1 > 105 || part2 < 49 || part2 > 57)
    {
        cout << "Nicht im Bereich!" << endl;
        return v;
    }

    part1 -= 97;
    part2 -= 49;
    v.push_back(part1);
    v.push_back(part2);
    return v;
}

//löscht rekursiv Element und Nachbarn
void removeElements(vector<vector<int>>& field, vector<int>& cell, int &counter)
{
    int letter = cell[0];
    int number = cell[1];
    int currentValue = field[letter][number];

    field[letter][number] = 0;
    counter ++;

    // Check right
    if (number + 1 < field[letter].size() && field[letter][number + 1] == currentValue)
    {
        vector<int> tempCell = {letter, number + 1};
        removeElements(field, tempCell, counter);
    }

    // Check left
    if (number - 1 >= 0 && field[letter][number - 1] == currentValue)
    {
        vector<int> tempCell = {letter, number - 1};
        removeElements(field, tempCell, counter);
    }

    // Check down
    if (letter + 1 < field.size() && field[letter + 1][number] == currentValue)
    {
        vector<int> tempCell = {letter + 1, number};
        removeElements(field, tempCell, counter);
    }

    // Check up
    if (letter - 1 >= 0 && field[letter - 1][number] == currentValue)
    {
        vector<int> tempCell = {letter - 1, number};
        removeElements(field, tempCell, counter);
    }
}

// prüft ob Element noch gleichfarbige Nachbarn hat
bool hasAdjacent(vector<vector<int>> &field, vector<int> &cell)
{
    int letter = cell[0];
    int number = cell[1];
    int currentValue = field[letter][number];

    if(currentValue == 0)
    {
        return false;
    }

    //right
    if(number < field[0].size()-1 && field[letter][number+1] == currentValue)
    {
        return true;
    }

    //left
    if(number > 0 && field[letter][number-1] == currentValue)
    {
        //removeElements(field, cell);
        return true;
    }

    //top
    if(letter < field.size() -1 && field[letter+1][number] == currentValue)
    {
        return true;
    }

    //down
    if(letter > 0 && field[letter-1][number] == currentValue)
    {
        return true;
    }

    return false;
}


void shrinkVertical(vector<vector<int>> &field)
{
    //Lässt Nullen nach oben sickern
    for(int i = 0; i< field.size(); i++)
    {
        for(int j= 0; j<field.size(); j++)
        {
            if(field[i][j] == 0)
            {
                for(int k = i; k>=0; k--)
                {
                    if(k == 0)
                    {
                        field[k][j] = 0;
                        break;
                    }
                    field[k][j] = field[k-1][j];
                }
            }
        }
    }

    //löscht leere Zeilen
    int i = 0;
    while (i < field.size())
    {
        bool empty = true;
        for (int j = 0; j < field[i].size(); j++)
        {
            if (field[i][j] != 0)
            {
                empty = false;
                break;
            }
        }
        if (empty)
        {
            field.erase(field.begin() + i);
        }
        else
        {
            i++;
        }
    }
}

void shrinkHorizontal(vector<vector<int>> &field)
{
    int i = 0;
    while (i < field.size())
    {
        bool empty = true;
        for (int j = 0; j < field.size(); j++)
        {
            if (field[j][i] != 0)
            {
                empty = false;
                break;
            }
        }
        if (empty)
        {
            for (int k = 0; k < field.size(); k++)
            {
                field[k].erase(field[k].begin() + i);
            }
        }
        else
        {
            i++;
        }
    }
}

// schaut ob irgendein Feld noch gleiche Nachbarn hat bzw. ob noch Züge möglich sind
bool gameOver(vector<vector<int>> &field)
{
    for(int i = 0; i<field.size(); i++)
    {
        for(int j = 0; j<field[i].size(); j++)
        {
            vector <int> currentField;
            currentField.push_back(i);
            currentField.push_back(j);
            if(hasAdjacent(field, currentField))
            {
                return false;
            }
        }
    }
    return true;
}

// Formel zur Punkteberechnung
int getPoints(int removedElements)
{
    return removedElements * (removedElements-1);
}
