
[CmdletBinding()]
param(
    [String]$formaEscriptura #La forma d'escriptura (string)
)
if ( -not $formaEscriptura -and ($formaEscriptura -ne "c" -And $formaEscriptura -ne "f")) {
    Write-Host "Entra la forma d'escriptura,'c' per escriure els resultats per consola, 'f' per escriure els resultats en fitxers per cada prova"
    while ( -not $formaEscriptura -and ($formaEscriptura -ne "c" -And $formaEscriptura -ne "f")) {
    $formaEscriptura = Read-Host "Enter un valor"
    }
}

$psi = New-Object System.Diagnostics.ProcessStartInfo;
$psi.FileName = "cmd.exe"; #process file
$psi.UseShellExecute = $false; #start the process from it's own executable file
$psi.RedirectStandardInput = $true; #enable the process to read from standard input
$p = [System.Diagnostics.Process]::Start($psi);

Start-Sleep -s 2 #wait 2 seconds so that the process can be up and running
$p.StandardInput.WriteLine("cd projecte-2020-a3\src");
Start-Sleep -s 1

$pastor1 = @(@("e7","e5"),@("b8","c6"),@("g8","f6"));
Start-Sleep -s 1
if($formaEscriptura -eq 'c'){$p.StandardInput.WriteLine("java ChessGame -c");}
else{$p.StandardInput.WriteLine("java ChessGame -c > testPastorBlanques.txt");}
Start-Sleep -s 1
$p.StandardInput.WriteLine("1");
Start-Sleep -s 1
$p.StandardInput.WriteLine("2");
Start-Sleep -s 1
$p.StandardInput.WriteLine("b");
Start-Sleep -s 1
$p.StandardInput.WriteLine("1");
Start-Sleep -s 1
$p.StandardInput.WriteLine("y");
Start-Sleep -s 1
$p.StandardInput.WriteLine("data\knowledge\pastorBlanques.json");
Start-Sleep -s 1
$p.StandardInput.WriteLine("exit");
Start-Sleep -s 1
foreach($jugada in $pastor1){
    foreach($moviment in $jugada){
        $p.StandardInput.WriteLine($moviment);
        Write-Host $moviment
        Start-Sleep -s 1
    }
    Start-Sleep -s 2
}


$pastor1Complementari = @(@("e7","e5"),@("b8","c6"),@("g7","g6"),@("c6","d4"));
if($formaEscriptura -eq 'c'){$p.StandardInput.WriteLine("java ChessGame -c");}
else{$p.StandardInput.WriteLine("java ChessGame -c > testPastorBlanquesComplementari.txt");}
Start-Sleep -s 1
$p.StandardInput.WriteLine("1");
Start-Sleep -s 1
$p.StandardInput.WriteLine("2");
Start-Sleep -s 1
$p.StandardInput.WriteLine("b");
Start-Sleep -s 1
$p.StandardInput.WriteLine("1");
Start-Sleep -s 1
$p.StandardInput.WriteLine("y");
Start-Sleep -s 1
$p.StandardInput.WriteLine("data\knowledge\pastorComplementariBlanques.json");
Start-Sleep -s 1
$p.StandardInput.WriteLine("exit");
Start-Sleep -s 1
foreach($jugada in $pastor1Complementari){
    foreach($moviment in $jugada){
        $p.StandardInput.WriteLine($moviment);
        Write-Host $moviment
        Start-Sleep -s 1
    }
    Start-Sleep -s 2
}

$pastor2 = @(@("e7","e5"),@("b8","c6"),@("g8","f6"));
if($formaEscriptura -eq 'c'){$p.StandardInput.WriteLine("java ChessGame -c");}
else{$p.StandardInput.WriteLine("java ChessGame -c > testPastor2Blanques.txt");}
Start-Sleep -s 1
$p.StandardInput.WriteLine("1");
Start-Sleep -s 1
$p.StandardInput.WriteLine("2");
Start-Sleep -s 1
$p.StandardInput.WriteLine("b");
Start-Sleep -s 1
$p.StandardInput.WriteLine("1");
Start-Sleep -s 1
$p.StandardInput.WriteLine("y");
Start-Sleep -s 1
$p.StandardInput.WriteLine("data\knowledge\pastor2Blanques.json");
Start-Sleep -s 1
$p.StandardInput.WriteLine("exit");
Start-Sleep -s 1
foreach($jugada in $pastor2){
    foreach($moviment in $jugada){
        $p.StandardInput.WriteLine($moviment);
        Write-Host $moviment
        Start-Sleep -s 1
    }
    Start-Sleep -s 2
}

$pastor3 = @(@("e7","e5"),@("b8","c6"),@("f8","c5"));
if($formaEscriptura -eq 'c'){$p.StandardInput.WriteLine("java ChessGame -c");}
else{$p.StandardInput.WriteLine("java ChessGame -c > testPastor3Blanques.txt");}
Start-Sleep -s 1
$p.StandardInput.WriteLine("1");
Start-Sleep -s 1
$p.StandardInput.WriteLine("2");
Start-Sleep -s 1
$p.StandardInput.WriteLine("b");
Start-Sleep -s 1
$p.StandardInput.WriteLine("1");
Start-Sleep -s 1
$p.StandardInput.WriteLine("y");
Start-Sleep -s 1
$p.StandardInput.WriteLine("data\knowledge\pastor3Blanques.json");
Start-Sleep -s 1
$p.StandardInput.WriteLine("exit");
Start-Sleep -s 1
foreach($jugada in $pastor3){
    foreach($moviment in $jugada){
        $p.StandardInput.WriteLine($moviment);
        Write-Host $moviment
        Start-Sleep -s 1
    }
    Start-Sleep -s 2
}

$matLocoNegres = @(@("f2","f3"),@("g2","g4"));
if($formaEscriptura -eq 'c'){$p.StandardInput.WriteLine("java ChessGame -c && exit");}
else{$p.StandardInput.WriteLine("java ChessGame -c > testMatdelLocoNegres.txt && exit");}
Start-Sleep -s 1
$p.StandardInput.WriteLine("1");
Start-Sleep -s 1
$p.StandardInput.WriteLine("2");
Start-Sleep -s 1
$p.StandardInput.WriteLine("w");
Start-Sleep -s 1
$p.StandardInput.WriteLine("1");
Start-Sleep -s 1
$p.StandardInput.WriteLine("y");
Start-Sleep -s 1
$p.StandardInput.WriteLine("data\knowledge\mateDelLocoNegres.json");
Start-Sleep -s 1
$p.StandardInput.WriteLine("exit");
Start-Sleep -s 1
foreach($jugada in $matLocoNegres){
    foreach($moviment in $jugada){
        $p.StandardInput.WriteLine($moviment);
        Write-Host $moviment
        Start-Sleep -s 1
    }
    Start-Sleep -s 2
    }
