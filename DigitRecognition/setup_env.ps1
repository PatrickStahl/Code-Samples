# Function to check if a command exists
function Test-Command($cmdname) {
    return [bool](Get-Command -Name $cmdname -ErrorAction SilentlyContinue)
}

# Function to create and activate virtual environment
function New-And-Activate-Venv($python_cmd) {
    $venv_name = ".digit_recognition_env"

    Write-Host "Creating virtual environment with $python_cmd..."
    & $python_cmd -m venv $venv_name

    $activate_script = Join-Path $venv_name "Scripts\Activate.ps1"

    Write-Host "Activating virtual environment..."
    & $activate_script
}

# Check for Python versions from 3.11 down to 3.8
$versions = @("3.11", "3.10", "3.9", "3.8")
$python_found = $false

foreach ($version in $versions) {
    if (Test-Command "python") {
        $python_version = & python -c "import sys; print(f'{sys.version_info.major}.{sys.version_info.minor}')"
        if ($python_version -eq $version) {
            New-And-Activate-Venv "python"
            $python_found = $true
            break
        }
    }
    if (Test-Command "python$version") {
        New-And-Activate-Venv "python$version"
        $python_found = $true
        break
    }
}

if (-not $python_found) {
    Write-Host "No compatible Python version found. Please install Python 3.8 or higher."
    exit 1
}

# Install dependencies
Write-Host "Installing dependencies..."
python -m pip install --upgrade pip
pip install -r .\requirements.txt

Write-Host "Virtual environment setup complete."
Write-Host "To activate the environment in the future, use:"
Write-Host ".\$venv_name\Scripts\Activate.ps1"
