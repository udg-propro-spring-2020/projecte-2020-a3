

$psi = New-Object System.Diagnostics.ProcessStartInfo;
$psi.FileName = "cmd.exe"; #process file
$psi.UseShellExecute = $false; #start the process from it's own executable file
$psi.RedirectStandardInput = $true; #enable the process to read from standard input
$p = [System.Diagnostics.Process]::Start($psi);

Start-Sleep -s 2 #wait 2 seconds so that the process can be up and running
$p.StandardInput.WriteLine("cd projecte-2020-a3\src");
Start-Sleep -s 1
$p.StandardInput.WriteLine("java ChessGame -c > testCPUwithKnowWhites_vs_cpuWithout.txt");

Start-Sleep -s 1
$p.StandardInput.WriteLine("1");
Start-Sleep -s 1
$p.StandardInput.WriteLine("3");
Start-Sleep -s 1
$p.StandardInput.WriteLine("1");
Start-Sleep -s 1
$p.StandardInput.WriteLine("1");
Start-Sleep -s 1
$p.StandardInput.WriteLine("y");
Start-Sleep -s 1
$fso = New-Object -ComObject Scripting.FileSystemObject;
$files = Get-ChildItem "..\src\data\knowledge\*.json"
foreach ($file in $files){
    $p.StandardInput.WriteLine("data\knowledge\"+$file.BaseName+".json");
    Start-Sleep -s 1
}
$p.StandardInput.WriteLine("exit");
