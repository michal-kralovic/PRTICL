Get-Content .env | ForEach-Object {
    $name, $value = $_.split('=', 2)

    if ([string]::IsNullOrWhiteSpace($name) -or $name.Contains('#')) {
        return
    }

    Write-Output "Processing $name = $value"
    Set-Content env:$name $value
}

Start-Process -FilePath "cmd.exe" -ArgumentList "/k cd /d $env:SERVER_PATH && start.bat" -WindowStyle Normal -WorkingDirectory "$env:SERVER_PATH"