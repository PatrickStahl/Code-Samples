cmake_minimum_required(VERSION 3.28)
project(DoodleJump)

include(FetchContent)
set(BUILD_SHARED_LIBS OFF)
FetchContent_Declare(SFML GIT_REPOSITORY https://github.com/SFML/SFML.git GIT_TAG 2.5.1)
FetchContent_MakeAvailable(SFML)

set(CMAKE_CXX_STANDARD 17)

add_executable(DoodleJump main.cpp)

# Das Ziel `DoodleJump` mit SFML-Bibliotheken verknüpfen
target_link_libraries(DoodleJump sfml-graphics sfml-window sfml-system)