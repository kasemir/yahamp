:: Back up RDB into *.sql file that can be sourced by mysql command-line
:: tool to restore tables and data:
::
:: Run
::   mysql -u yahamp -p$yahamp yahamp <yahamp_dump.sql
::
:: or type inside mysql:
::   use yahamp;
::   source /path/to/dump.sql

"C:\Program Files\MySQL\MySQL Server 5.5\bin\mysqldump.exe" -u yahamp -p"$yahamp" yahamp >%USERPROFILE%\Documents\yahamp_dump.sql