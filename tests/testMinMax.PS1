
For($i=1; $i -le 3; $i++) {
    For($j=1; $j -le 3; $j++) {
        $psi = New-Object System.Diagnostics.ProcessStartInfo;
        $psi.FileName = "cmd.exe"; #process file
        $psi.UseShellExecute = $false; #start the process from it's own executable file
        $psi.RedirectStandardInput = $true; #enable the process to read from standard input
        $p = [System.Diagnostics.Process]::Start($psi);
        $p.StandardInput.WriteLine("cd projecte-2020-a3\src");
        Start-Sleep -s 1
        #> testMinMax_niv"+$i+"+_vs_niv"+$j+".txt
        $p.StandardInput.WriteLine("java ChessGame -c > testMinMax_niv"+$i+"_vs_niv"+$j+".txt && exit");
        Start-Sleep -s 1
        $p.StandardInput.WriteLine("1");
        Start-Sleep -s 1
        $p.StandardInput.WriteLine("3");
        Start-Sleep -s 1
        $p.StandardInput.WriteLine($i);
        Start-Sleep -s 1
        $p.StandardInput.WriteLine($j);
        Start-Sleep -s 1
        $p.StandardInput.WriteLine("n");
    }
}

