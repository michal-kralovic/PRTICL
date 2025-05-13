function Get-TabbableTerminals {
  Add-Type -TypeDefinition @"
  using System;
  using System.Runtime.InteropServices;
  using System.Text;

  public class WindowInfo {
      [DllImport("user32.dll", SetLastError = true)]
      public static extern bool EnumWindows(EnumWindowsProc enumProc, IntPtr lParam);
      
      [DllImport("user32.dll", SetLastError = true)]
      public static extern int GetWindowText(IntPtr hWnd, StringBuilder lpString, int nMaxCount);
      
      [DllImport("user32.dll", SetLastError = true)]
      public static extern int GetWindowTextLength(IntPtr hWnd);
      
      [DllImport("user32.dll", SetLastError = true)]
      public static extern bool IsWindowVisible(IntPtr hWnd);
      
      [DllImport("user32.dll", SetLastError = true)]
      public static extern uint GetWindowThreadProcessId(IntPtr hWnd, out uint processId);
      
      [DllImport("user32.dll", SetLastError = true)]
      public static extern IntPtr GetShellWindow();
      
      [DllImport("user32.dll", SetLastError = true)]
      public static extern IntPtr GetForegroundWindow();
      
      public delegate bool EnumWindowsProc(IntPtr hWnd, IntPtr lParam);
  }
"@

  $terminalProcesses = @(
      "WindowsTerminal",
      "powershell",
      "pwsh",
      "cmd",
      "ConEmu",
      "ConEmu64",
      "mintty",
      "alacritty",
      "wezterm-gui",
      "hyper",
      "tabby",
      "terminus"
  )

  $terminalWindows = New-Object System.Collections.ArrayList

  $enumWindowsCallback = [WindowInfo+EnumWindowsProc] {
      param(
          [IntPtr]$hWnd,
          [IntPtr]$lParam
      )

      if (-not [WindowInfo]::IsWindowVisible($hWnd)) {
          return $true
      }

      $length = [WindowInfo]::GetWindowTextLength($hWnd)
      if ($length -eq 0) {
          return $true
      }

      $title = New-Object System.Text.StringBuilder($length + 1)
      [void][WindowInfo]::GetWindowText($hWnd, $title, $title.Capacity)

      $processId = 0
      [void][WindowInfo]::GetWindowThreadProcessId($hWnd, [ref]$processId)

      try {
          $process = Get-Process -Id $processId -ErrorAction SilentlyContinue
          if ($process -and ($terminalProcesses -contains $process.ProcessName)) {
              $windowInfo = [PSCustomObject]@{
                  Handle = $hWnd
                  Title = $title.ToString()
                  ProcessId = $processId
                  ProcessName = $process.ProcessName
                  Path = $process.Path
                  IsForeground = $hWnd -eq [WindowInfo]::GetForegroundWindow()
              }
              [void]$terminalWindows.Add($windowInfo)
          }
      }
      catch {
          # Skip if process can't be accessed (e.g., elevated permissions)
      }

      return $true
  }

  [void][WindowInfo]::EnumWindows($enumWindowsCallback, [IntPtr]::Zero)

  return $terminalWindows
}

# Execute the function and display results
$terminals = Get-TabbableTerminals

if ($terminals.Count -eq 0) {
  Write-Host "No tabbable terminal windows found." -ForegroundColor Yellow
}
else {
  Write-Host "Found $($terminals.Count) tabbable terminal windows:" -ForegroundColor Cyan
  $terminals | Format-Table -Property ProcessName, Title, ProcessId, IsForeground -AutoSize
}

$thatTerminal = $terminals | Where-Object { $_.Title -like "*start.bat" }
if ($null -ne $thatTerminal.ProcessId) {
  Stop-Process -Id $thatTerminal.ProcessId -Force -ErrorAction Stop
} else {
  Write-Host "No terminal to slaughter"
}