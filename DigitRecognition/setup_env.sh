#!/bin/bash

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to create and activate virtual environment
create_and_activate_venv() {
    python_cmd=$1
    venv_name=".digit_recognition_env"

    echo "Creating virtual environment with $python_cmd..."
    $python_cmd -m venv $venv_name

    # Determine the correct activation script
    if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
        activate_script="$venv_name/Scripts/activate"
    else
        activate_script="$venv_name/bin/activate"
    fi

    echo "Activating virtual environment..."
    source "$activate_script"
}

# Check for Python versions from 3.11 down to 3.8
for version in 3.11 3.10 3.9 3.8; do
    if command_exists "python$version"; then
        create_and_activate_venv "python$version"
        break
    elif command_exists "python3.$version"; then
        create_and_activate_venv "python3.$version"
        break
    elif [ $version == "3.8" ]; then
        echo "No compatible Python version found. Please install Python 3.8 or higher."
        exit 1
    fi
done

# Install dependencies
echo "Installing dependencies..."
python -m pip install --upgrade pip
pip install -r requirements.txt

echo "Virtual environment setup complete."
echo "To activate the environment in the future, use:"
echo "source $activate_script"
