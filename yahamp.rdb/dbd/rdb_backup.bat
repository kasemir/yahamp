

"\Program Files\MySQL\MySQL Server 5.5\bin\mysqldump.exe" -u yahamp -p$yahamp yahamp >e:\yahamp_dump.sql

# To read back:
# In mysql shell,
# use yahamp;
# and then source the dump file.
# (might have to specify the full path to the file)
source create_yahamp.sql