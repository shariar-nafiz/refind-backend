-- Flyway Migration: V6__seed_sample_items_and_users.sql
-- Description: Seed realistic users, media assets, lost & found items, and media associations

SET timezone = 'UTC';

-- ---------------------------------------------------------------------
-- 1. Seed Sample Users
-- ---------------------------------------------------------------------
INSERT INTO users (email, phone, password_hash, full_name, role, status)
VALUES
    ('admin@refind.com', '+8801700000001', '$2a$10$feda0EB8.XDCeyTBrznWR.6zMhgg6sYmqcs4QxPvAhmB0FJJ18UNu', 'Shariar Nafiz (Admin)', 'ROLE_ADMIN', 'ACTIVE'),
    ('shariar.nafiz@gmail.com', '+8801700000002', '$2a$10$feda0EB8.XDCeyTBrznWR.6zMhgg6sYmqcs4QxPvAhmB0FJJ18UNu', 'Shariar Nafiz', 'ROLE_USER', 'ACTIVE'),
    ('tanvir.hasan@gmail.com', '+8801700000003', '$2a$10$feda0EB8.XDCeyTBrznWR.6zMhgg6sYmqcs4QxPvAhmB0FJJ18UNu', 'Tanvir Hasan', 'ROLE_USER', 'ACTIVE')
ON CONFLICT (email) DO NOTHING;

-- ---------------------------------------------------------------------
-- 2. Seed Media Assets
-- ---------------------------------------------------------------------
INSERT INTO media (user_id, file_name, file_type, file_size, file_url, storage_path)
VALUES
    ((SELECT id FROM users WHERE email = 'shariar.nafiz@gmail.com' LIMIT 1),
     'wallet_leather.jpg', 'image/jpeg', 245100,
     'https://images.unsplash.com/photo-1627123424574-724758594e93?w=800&auto=format&fit=crop&q=80',
     'seed/wallet_leather.jpg'),

    ((SELECT id FROM users WHERE email = 'tanvir.hasan@gmail.com' LIMIT 1),
     'iphone_blue.jpg', 'image/jpeg', 312800,
     'https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=800&auto=format&fit=crop&q=80',
     'seed/iphone_blue.jpg'),

    ((SELECT id FROM users WHERE email = 'shariar.nafiz@gmail.com' LIMIT 1),
     'dell_charger.jpg', 'image/jpeg', 189400,
     'https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=800&auto=format&fit=crop&q=80',
     'seed/dell_charger.jpg'),

    ((SELECT id FROM users WHERE email = 'tanvir.hasan@gmail.com' LIMIT 1),
     'nid_card.jpg', 'image/jpeg', 205600,
     'https://images.unsplash.com/photo-1589829545856-d10d557cf95f?w=800&auto=format&fit=crop&q=80',
     'seed/nid_card.jpg'),

    ((SELECT id FROM users WHERE email = 'shariar.nafiz@gmail.com' LIMIT 1),
     'northface_backpack.jpg', 'image/jpeg', 421000,
     'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800&auto=format&fit=crop&q=80',
     'seed/northface_backpack.jpg'),

    ((SELECT id FROM users WHERE email = 'tanvir.hasan@gmail.com' LIMIT 1),
     'yamaha_keys.jpg', 'image/jpeg', 165000,
     'https://images.unsplash.com/photo-1603584173870-7f23fdae1b7a?w=800&auto=format&fit=crop&q=80',
     'seed/yamaha_keys.jpg'),

    ((SELECT id FROM users WHERE email = 'shariar.nafiz@gmail.com' LIMIT 1),
     'casio_watch.jpg', 'image/jpeg', 298700,
     'https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=800&auto=format&fit=crop&q=80',
     'seed/casio_watch.jpg'),

    ((SELECT id FROM users WHERE email = 'shariar.nafiz@gmail.com' LIMIT 1),
     'rayban_sunglasses.jpg', 'image/jpeg', 234500,
     'https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=800&auto=format&fit=crop&q=80',
     'seed/rayban_sunglasses.jpg');

-- ---------------------------------------------------------------------
-- 3. Seed Items (Lost & Found)
-- ---------------------------------------------------------------------
INSERT INTO items (user_id, category_id, location_id, type, title, description, specific_location_hint, incident_date_time, secret_identifier_question, secret_identifier_answer, tags, status, view_count)
VALUES
    -- 1. Lost Wallet (Dhanmondi, Dhaka)
    ((SELECT id FROM users WHERE email = 'shariar.nafiz@gmail.com' LIMIT 1),
     (SELECT id FROM categories WHERE slug = 'wallets-purses' LIMIT 1),
     (SELECT id FROM locations WHERE thana = 'Dhanmondi' AND district = 'Dhaka' LIMIT 1),
     'LOST',
     'Lost Black Leather Wallet with NID & Cards',
     'Lost a black bifold leather wallet containing my National ID, BRAC Bank credit card, and some cash. It fell out of my pocket while boarding a rickshaw near Dhanmondi 27.',
     'Near Dhanmondi 27 bus stand, in front of Meena Bazar',
     CURRENT_TIMESTAMP - INTERVAL '2 days',
     'What brand name is embossed inside the right fold of the wallet?',
     'Apex',
     ARRAY['wallet', 'leather', 'nid', 'dhanmondi', 'cards'],
     'OPEN',
     42),

    -- 2. Found iPhone (Gulshan, Dhaka)
    ((SELECT id FROM users WHERE email = 'tanvir.hasan@gmail.com' LIMIT 1),
     (SELECT id FROM categories WHERE slug = 'phones-tablets' LIMIT 1),
     (SELECT id FROM locations WHERE thana = 'Gulshan' AND district = 'Dhaka' LIMIT 1),
     'FOUND',
     'Found iPhone 13 in Navy Blue Silicone Case',
     'Found an iPhone 13 left unattended on an outdoor table at a coffee shop. The phone is locked and running low on battery. Screen has a glass protector with a tiny hairline scratch.',
     'Outdoor patio table at Gloria Jeans, Gulshan 1',
     CURRENT_TIMESTAMP - INTERVAL '1 day',
     'What character or image is shown on the lock screen wallpaper?',
     'Cat on a skateboard',
     ARRAY['iphone', 'apple', 'smartphone', 'gulshan', 'blue'],
     'OPEN',
     89),

    -- 3. Lost Dell Charger (Mirpur, Dhaka)
    ((SELECT id FROM users WHERE email = 'shariar.nafiz@gmail.com' LIMIT 1),
     (SELECT id FROM categories WHERE slug = 'laptops-electronics' LIMIT 1),
     (SELECT id FROM locations WHERE thana = 'Mirpur' AND district = 'Dhaka' LIMIT 1),
     'LOST',
     'Lost Dell XPS 15 Laptop Charger and Mouse',
     'Misplaced my original Dell 130W USB-C barrel adapter and a black Logitech wireless mouse inside a small mesh pouch on the metro rail platform.',
     'Mirpur 10 Metro Station Concourse ticketing counter',
     CURRENT_TIMESTAMP - INTERVAL '3 days',
     'What tech sticker is affixed to the back of the charging brick?',
     'GitHub Octocat',
     ARRAY['dell', 'charger', 'laptop', 'mirpur', 'metro'],
     'OPEN',
     19),

    -- 4. Found NID Card & Student ID (Panchlaish, Chattogram)
    ((SELECT id FROM users WHERE email = 'tanvir.hasan@gmail.com' LIMIT 1),
     (SELECT id FROM categories WHERE slug = 'identity-cards' LIMIT 1),
     (SELECT id FROM locations WHERE thana = 'Panchlaish' AND district = 'Chattogram' LIMIT 1),
     'FOUND',
     'Found National ID Card & University Student ID',
     'Discovered a laminated Bangladesh National ID card alongside an IIUC university student identity card inside a clear plastic card protector.',
     'Near GEC Circle footpath by Sanmar Ocean City',
     CURRENT_TIMESTAMP - INTERVAL '4 days',
     'What is the academic department printed on the university student ID card?',
     'Computer Science',
     ARRAY['nid', 'student-id', 'chattogram', 'gec', 'identity'],
     'OPEN',
     57),

    -- 5. Lost Backpack (Kotwali, Sylhet)
    ((SELECT id FROM users WHERE email = 'shariar.nafiz@gmail.com' LIMIT 1),
     (SELECT id FROM categories WHERE slug = 'bags-luggage' LIMIT 1),
     (SELECT id FROM locations WHERE thana = 'Kotwali (Sylhet)' AND district = 'Sylhet' LIMIT 1),
     'LOST',
     'Lost Black North Face Backpack',
     'Left my black The North Face backpack containing semester class lecture notes and a water bottle in a CNG auto-rickshaw while travelling from Amberkhana to Zindabazar.',
     'Dropped inside CNG auto-rickshaw between Amberkhana and Zindabazar',
     CURRENT_TIMESTAMP - INTERVAL '5 days',
     'What specific souvenir keychain is hooked onto the top front zipper?',
     'Red compass keychain',
     ARRAY['backpack', 'northface', 'bag', 'sylhet', 'zindabazar'],
     'OPEN',
     31),

    -- 6. Found Motorcycle Keys (Uttara East, Dhaka)
    ((SELECT id FROM users WHERE email = 'tanvir.hasan@gmail.com' LIMIT 1),
     (SELECT id FROM categories WHERE slug = 'keys-keychains' LIMIT 1),
     (SELECT id FROM locations WHERE thana = 'Uttara East' AND district = 'Dhaka' LIMIT 1),
     'FOUND',
     'Found Set of Yamaha Motorcycle Keys',
     'Found a set of two keys on a ring, one of which is a Yamaha motorcycle ignition key with a braided black leather strap.',
     'Sector 7 park walking track, on a wooden bench near the lake',
     CURRENT_TIMESTAMP - INTERVAL '12 hours',
     'What code or text is engraved on the small metal tag attached to the key ring?',
     'FZS-2022',
     ARRAY['keys', 'yamaha', 'motorcycle', 'uttara', 'keychain'],
     'OPEN',
     76),

    -- 7. Found Casio Watch (Boalia, Rajshahi)
    ((SELECT id FROM users WHERE email = 'shariar.nafiz@gmail.com' LIMIT 1),
     (SELECT id FROM categories WHERE slug = 'jewelry-watches' LIMIT 1),
     (SELECT id FROM locations WHERE thana = 'Boalia' AND district = 'Rajshahi' LIMIT 1),
     'FOUND',
     'Found Casio Vintage Digital Watch',
     'Found a stainless steel vintage silver Casio digital wrist watch. The clasp is slightly loose but the timekeeping and backlight are fully functional.',
     'Padma river bank walkway near Boro Kuthi',
     CURRENT_TIMESTAMP - INTERVAL '6 days',
     'What backlight color illuminates the screen when the light button is pressed?',
     'Amber gold',
     ARRAY['casio', 'watch', 'digital', 'rajshahi', 'padma'],
     'OPEN',
     25),

    -- 8. Lost Sunglasses (Cox''s Bazar Sadar)
    ((SELECT id FROM users WHERE email = 'shariar.nafiz@gmail.com' LIMIT 1),
     (SELECT id FROM categories WHERE slug = 'clothing-eyewear' LIMIT 1),
     (SELECT id FROM locations WHERE thana = 'Cox''s Bazar Sadar' AND district = 'Cox''s Bazar' LIMIT 1),
     'LOST',
     'Lost Ray-Ban Aviator Sunglasses in Black Hard Case',
     'Gold-framed Ray-Ban aviator sunglasses with polarized dark green lenses in a black leatherette snap case. Accidentally left on a beach lounge chair under an umbrella.',
     'Laboni Beach, lounge chair row #3 umbrella #42',
     CURRENT_TIMESTAMP - INTERVAL '1 day',
     'What exact color and coating are the lenses?',
     'Polarized green',
     ARRAY['glasses', 'sunglasses', 'rayban', 'coxsbazar', 'beach'],
     'OPEN',
     64);

-- ---------------------------------------------------------------------
-- 4. Associate Items with Media
-- ---------------------------------------------------------------------
INSERT INTO item_media (item_id, media_id, is_primary, display_order)
VALUES
    ((SELECT id FROM items WHERE title = 'Lost Black Leather Wallet with NID & Cards' LIMIT 1),
     (SELECT id FROM media WHERE file_name = 'wallet_leather.jpg' LIMIT 1),
     TRUE, 0),

    ((SELECT id FROM items WHERE title = 'Found iPhone 13 in Navy Blue Silicone Case' LIMIT 1),
     (SELECT id FROM media WHERE file_name = 'iphone_blue.jpg' LIMIT 1),
     TRUE, 0),

    ((SELECT id FROM items WHERE title = 'Lost Dell XPS 15 Laptop Charger and Mouse' LIMIT 1),
     (SELECT id FROM media WHERE file_name = 'dell_charger.jpg' LIMIT 1),
     TRUE, 0),

    ((SELECT id FROM items WHERE title = 'Found National ID Card & University Student ID' LIMIT 1),
     (SELECT id FROM media WHERE file_name = 'nid_card.jpg' LIMIT 1),
     TRUE, 0),

    ((SELECT id FROM items WHERE title = 'Lost Black North Face Backpack' LIMIT 1),
     (SELECT id FROM media WHERE file_name = 'northface_backpack.jpg' LIMIT 1),
     TRUE, 0),

    ((SELECT id FROM items WHERE title = 'Found Set of Yamaha Motorcycle Keys' LIMIT 1),
     (SELECT id FROM media WHERE file_name = 'yamaha_keys.jpg' LIMIT 1),
     TRUE, 0),

    ((SELECT id FROM items WHERE title = 'Found Casio Vintage Digital Watch' LIMIT 1),
     (SELECT id FROM media WHERE file_name = 'casio_watch.jpg' LIMIT 1),
     TRUE, 0),

    ((SELECT id FROM items WHERE title = 'Lost Ray-Ban Aviator Sunglasses in Black Hard Case' LIMIT 1),
     (SELECT id FROM media WHERE file_name = 'rayban_sunglasses.jpg' LIMIT 1),
     TRUE, 0)
ON CONFLICT (item_id, media_id) DO NOTHING;
