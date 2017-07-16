USE ad;


CREATE TABLE IF NOT EXISTS Item_xy(
	item_id INTEGER NOT NULL,
	xy POINT NOT NULL,

	PRIMARY KEY(item_id)
) ENGINE= MyISAM;

INSERT INTO Item_xy(item_id,xy) SELECT item_id, POINT(longitude,latitude) FROM item_coordinates;

CREATE SPATIAL INDEX xy_index ON Item_xy(xy);

