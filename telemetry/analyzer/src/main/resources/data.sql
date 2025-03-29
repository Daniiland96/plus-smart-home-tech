delete from scenario_actions;
delete from scenario_conditions;
delete from actions;
delete from conditions;
delete from scenarios;
delete from sensors;

ALTER TABLE actions  ALTER COLUMN id RESTART WITH 1;
ALTER TABLE conditions  ALTER COLUMN id RESTART WITH 1;
ALTER TABLE scenarios  ALTER COLUMN id RESTART WITH 1;