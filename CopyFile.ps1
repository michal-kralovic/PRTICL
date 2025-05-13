param(
    [Parameter(Mandatory=$true)]
    [string]$BuildDirectory,
    
    [Parameter(Mandatory=$true)]
    [string]$BuildFinalName
)

Get-Content .env | ForEach-Object {
  $name, $value = $_.split('=', 2)

  if ([string]::IsNullOrWhiteSpace($name) -or $name.Contains('#')) {
      return
  }

  Write-Output "Processing $name = $value"
  Set-Content env:$name $value
}

$serverPath = $env:SERVER_PATH

if (-not $serverPath) {
  Write-Error "SERVER_PATH environment variable is not set"
  exit 1
}

$pluginsPath = Join-Path -Path $serverPath -ChildPath "plugins"

$sourceJar = Join-Path -Path $BuildDirectory -ChildPath "$BuildFinalName.jar"
$destinationJar = Join-Path -Path $pluginsPath -ChildPath "$BuildFinalName.jar"

Write-Host "Copying $sourceJar to $destinationJar"
Copy-Item -Path $sourceJar -Destination $destinationJar -Force

Write-Host "Successfully copied plugin to server plugins directory"