#include <SFML/Graphics.hpp>
#include <time.h>
#include <Windows.h>
#include <filesystem> 
using namespace sf;

// Function to get the executable's directory path
std::string getExecutablePath() {
    char path[MAX_PATH];
    GetModuleFileNameA(NULL, path, MAX_PATH);  // Get the full path of the executable
    std::filesystem::path exePath(path);       // Convert to std::filesystem::path
    return exePath.parent_path().string();     // Return the parent directory as a string
}

struct point { int x, y; };

struct Enemy {
    Sprite sprite;
    int x, y;  // Gegner hat x und y Koordinaten
    bool active;
};

int main() {
    srand(time(0));

    RenderWindow app(VideoMode(400, 533), "Doodle Jump", Style::Close);
    app.setFramerateLimit(60);

    // Get the executable's directory
    std::string exeDir = getExecutablePath();

    // Bilder laden with paths relative to the executable's location
    Texture background, plattform, doodler_left, doodler_right, doodleenemy;
    
    // Construct paths relative to the executable directory
    background.loadFromFile(exeDir + "/img/background.png");
    plattform.loadFromFile(exeDir + "/img/platform.png");
    doodler_left.loadFromFile(exeDir + "/img/doodler_left.png");
    doodler_right.loadFromFile(exeDir + "/img/doodler_right.png");
    doodleenemy.loadFromFile(exeDir + "/img/enemy.png");

    // Endscreen konfigurieren
    Sprite sBackground(background), sPlat(plattform), sPers(doodler_right);
    Font font;
    font.loadFromFile(exeDir + "/fonts/DoodleJump.ttf");
    Text gameOverText("\t\tGame Over!\n Press Enter to Restart", font, 30);
    gameOverText.setFillColor(Color::Black);
    FloatRect textRect = gameOverText.getLocalBounds();
    gameOverText.setOrigin(textRect.left + textRect.width / 2.0f,
                           textRect.top + textRect.height / 2.0f);
    gameOverText.setPosition(400 / 2.0f, 533 / 2.0f);

    // 20 Plattformen und einen Gegner spawnen
    point plat[20];
    Enemy enemy;
    int x = 100, y = 100, h = 200;
    float dx = 0, dy = 0;
    bool isGameOver = false;

    // Plattformen initialisieren
    for (int i = 0; i < 10; i++) {
        plat[i].x = rand() % 400;
        plat[i].y = rand() % 533;
    }

    // Gegner initialisieren
    enemy.sprite.setTexture(doodleenemy);
    enemy.x = 100 + rand() % 200;
    enemy.y = 0;
    enemy.sprite.setPosition(enemy.x, enemy.y);
    enemy.sprite.setScale(0.7f, 0.7f);
    enemy.active = true;

    while (app.isOpen()) {
        Event e;
        while (app.pollEvent(e)) {
            if (e.type == Event::Closed)
                app.close();
        }

        if (isGameOver) {
            if (Keyboard::isKeyPressed(Keyboard::Enter)) {
                isGameOver = false;
                x = 100;
                y = 100;
                dy = 0;

                // Plattformen zurücksetzen
                for (int i = 0; i < 10; i++) {
                    plat[i].x = rand() % 400;
                    plat[i].y = rand() % 533;
                }

                // Gegner zurücksetzen mit einer Wahrscheinlichkeit
                if (rand() % 4 == 0) {  // 50% Chance für das Spawnen
                    enemy.active = true;
                    enemy.x = 100 + rand() % 200;
                    enemy.y = 0;
                    enemy.sprite.setPosition(enemy.x, enemy.y);
                } else {
                    enemy.active = false;  // Gegner wird nicht gespawnt
                }
            }

            app.clear();
            app.draw(sBackground);
            app.draw(gameOverText);
            app.display();
            continue;
        }

        // Spielerbewegungen
        if (Keyboard::isKeyPressed(Keyboard::Right)){
            x += 3;
            sPers.setTexture(doodler_right);
        }
        if (Keyboard::isKeyPressed(Keyboard::Left)){
            x -= 3;
            sPers.setTexture(doodler_left);
        }

        if (x < -45) x = 395;
        if (x > 395) x = -40;

        dy += 0.2;
        y += dy;

        if (y > 533) {
            isGameOver = true;
        }

        // Plattformen und Gegner bewegen, wenn der Spieler aufsteigt
        if (y < h) {
            for (int i = 0; i < 10; i++) {
                y = h;
                plat[i].y = plat[i].y - dy;
                if (plat[i].y > 533) {
                    plat[i].y = 0;
                    plat[i].x = rand() % 400;
                }
            }

            if (enemy.active) {
                enemy.y = enemy.y - dy;
                if (enemy.y > 533) {
                    enemy.active = false;
                }
                enemy.sprite.setPosition(enemy.x, enemy.y);
            }
        }

        // Gegner mit einer gewissen Wahrscheinlichkeit wieder spawnen, wenn er nicht aktiv ist (2% pro Frame)
        if (!enemy.active && rand() % 100 < 2) {
            enemy.active = true;
            enemy.x = 100 + rand() % 200;
            enemy.y = 0;
            enemy.sprite.setPosition(enemy.x, enemy.y);
        }

        // Kollision mit Plattform
        for (int i = 0; i < 10; i++) {
            if ((x + 50 > plat[i].x) && (x + 20 < plat[i].x + 68) &&
                (y + 70 > plat[i].y) && (y + 70 < plat[i].y + 14) && (dy > 0))
                dy = -10;
        }

        // Kollision mit Gegner
        if (enemy.active) {
            FloatRect playerBounds(x, y, 50, 70);
            FloatRect enemyBounds = enemy.sprite.getGlobalBounds();
            if (playerBounds.intersects(enemyBounds)) {
                isGameOver = true;
            }
        }

        // Spielerposition aktualisieren
        sPers.setPosition(x, y);
        app.clear();
        app.draw(sBackground);
        app.draw(sPers);
        for (int i = 0; i < 10; i++) {
            sPlat.setPosition(plat[i].x, plat[i].y);
            app.draw(sPlat);
        }

        // Gegnerposition aktualisieren, falls aktiv
        if (enemy.active) {
            app.draw(enemy.sprite);
        }

        app.display();
    }

    return 0;
}
