# POO_FinalProject_Quintero_Narvaez

Integrantes:
Integrantes:
- Isabela Quintero - 
- Isabela Narvaez - 

Sustituye «Estudiante uno» y «Estudiante dos» por los nombres y códigos reales de los miembros del equipo antes de enviar el trabajo.

Descripcion general:
Esta aplicación simula un sistema interno de manejo de una tienda de videojuegos. Permite a los empleados registrar videojuegos, clientes y ventas. Los datos se almacenan de forma permanente mediante la serialización de Java.

Run instructions:
1. Compile Java files under `src/`.
2. Run `Main`.

Quick run (PowerShell):
```powershell
mkdir out
Get-ChildItem -Recurse -Filter *.java | ForEach-Object { & javac -d out $_.FullName }
java -cp out Main
```

Example I/O:
- Add game -> provide id/title/category/price
- Register sale -> select a game and enter customer id

Notes:
- Java 21 is targeted. All source code and comments are in English.
