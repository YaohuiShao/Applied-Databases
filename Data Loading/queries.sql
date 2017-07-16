USE ad;

select count(*) from ((select UserID from Seller) union (select UserID from Bidder)) as count_users;

select count(*) as item_loc_NYC from Item where binary Location='New York';

select count(*) from (select ItemID from ItemCategory group by ItemID having count(*)=4) as item_4_cat;

select item.ItemID from Item item, Bids bid where Ends > "2001-12-20 00:00:00" AND item.ItemID = bid.ItemID
order by Currently DESC LIMIT 1;

select count(*) as srating_1000 from Seller where Rating>1000;

select count(*) from (select UserID from Seller natural join Bidder) as both_user;

select count(*) as num_category from (select count(*) from Bids bids,ItemCategory icat where bids.ItemID=icat.ItemID and Amount>100 group by Category) as num_category;
