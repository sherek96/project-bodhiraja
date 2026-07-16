ALTER TABLE guardian
    ADD COLUMN dob DATE NULL AFTER nic,
    ADD COLUMN gender VARCHAR(20) NULL AFTER dob;

ALTER TABLE student
    ADD COLUMN ordination_name VARCHAR(255) NULL AFTER date_of_ordination,
    ADD COLUMN previous_school VARCHAR(255) NULL AFTER ordination_name;
