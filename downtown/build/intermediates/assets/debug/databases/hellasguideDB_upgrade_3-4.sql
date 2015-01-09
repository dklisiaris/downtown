BEGIN TRANSACTION;


CREATE TABLE banners
(
    _id INTEGER PRIMARY KEY NOT NULL,
    name VARCHAR NOT NULL,
    weight INTEGER NOT NULL,
    category_id INTEGER,
    FOREIGN KEY (category_id) REFERENCES categories(_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Creating table categories from scratch (simple ALTER TABLE is not enough)

CREATE TABLE temp_categories_534011718
(
    _id INTEGER PRIMARY KEY NOT NULL DEFAULT (NULL),
    cat_name VARCHAR NOT NULL,
    cat_parent_id INTEGER DEFAULT (NULL),
    cat_icon VARCHAR DEFAULT NULL
);

-- Copying rows from original table to the new table

INSERT INTO temp_categories_534011718 (_id,cat_name,cat_parent_id,cat_icon) SELECT _id,cat_name,cat_parent_id,NULL AS cat_icon FROM categories;

-- Droping the original table and renaming the temporary table

DROP TABLE categories;
ALTER TABLE temp_categories_534011718 RENAME TO categories;

CREATE TABLE category_keyword_map
(
    category_id INTEGER NOT NULL,
    keyword_id INTEGER NOT NULL,
    FOREIGN KEY (category_id) REFERENCES categories(_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (keyword_id) REFERENCES keywords(_id) ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY (category_id,keyword_id)
);

-- Creating table company from scratch (simple ALTER TABLE is not enough)

CREATE TABLE temp_company_1655911537
(
    _id INTEGER PRIMARY KEY NOT NULL DEFAULT (NULL),
    co_name VARCHAR NOT NULL DEFAULT (NULL),
    co_address VARCHAR DEFAULT (NULL),
    co_tel VARCHAR DEFAULT (NULL),
    co_description TEXT,
    co_image VARCHAR DEFAULT (NULL),
    co_category INTEGER DEFAULT (NULL),
    co_subcategory VARCHAR DEFAULT (NULL),
    co_area VARCHAR DEFAULT (NULL),
    co_availability INTEGER,
    co_website VARCHAR DEFAULT (NULL),
    co_latitude REAL DEFAULT (NULL),
    co_longitude REAL DEFAULT (NULL),
    co_level INTEGER NOT NULL DEFAULT (0),
    co_county VARCHAR DEFAULT (NULL),
    co_tk VARCHAR DEFAULT (NULL),
    co_fax VARCHAR
);

-- Copying rows from original table to the new table

INSERT INTO temp_company_1655911537 (_id,co_name,co_address,co_tel,co_description,co_image,co_category,co_subcategory,co_area,co_availability,co_website,co_level,co_county,co_tk,co_fax,co_latitude,co_longitude) SELECT _id,co_name,co_address,co_tel,co_description,co_image,co_category,co_subcategory,co_area,co_availability,co_website,co_level,co_county,co_tk,co_fax,NULL AS co_latitude,NULL AS co_longitude FROM company;

-- Droping the original table and renaming the temporary table

DROP TABLE company;
ALTER TABLE temp_company_1655911537 RENAME TO company;

CREATE TABLE company_category_map
(
    company_id INTEGER NOT NULL,
    category_id INTEGER NOT NULL,
    FOREIGN KEY (company_id) REFERENCES company(_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(_id) ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY (company_id,category_id)
);

CREATE TABLE company_keyword_map
(
    company_id INTEGER NOT NULL,
    keyword_id INTEGER NOT NULL,
    FOREIGN KEY (company_id) REFERENCES company(_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (keyword_id) REFERENCES keywords(_id) ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY (company_id,keyword_id)
);

CREATE TABLE images
(
    _id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    name VARCHAR NOT NULL,
    weight INTEGER NOT NULL,
    company_id INTEGER NOT NULL,
    FOREIGN KEY (company_id) REFERENCES company(_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE keywords
(
    _id INTEGER PRIMARY KEY NOT NULL,
    keyword NOT NULL
);

COMMIT TRANSACTION;
