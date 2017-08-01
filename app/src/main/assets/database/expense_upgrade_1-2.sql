BEGIN TRANSACTION;
update expense set date = substr(date,7,4)||"-"||substr(date,4,2)||"-"||substr(date,1,2);
COMMIT;
