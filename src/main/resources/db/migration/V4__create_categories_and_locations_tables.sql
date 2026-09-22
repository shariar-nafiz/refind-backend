-- Flyway Migration: V4__create_categories_and_locations_tables.sql
-- Description: Create categories and locations tables with seed data for 10 core categories and all 64 Bangladesh districts & thanas

SET timezone = 'UTC';

-- ---------------------------------------------------------------------
-- 1. Table: categories
-- Description: Item classification categories with icons and display ordering
-- ---------------------------------------------------------------------
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(100) NOT NULL UNIQUE,
    icon_name VARCHAR(100),
    description VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE categories IS 'Classification categories for lost and found items';
COMMENT ON COLUMN categories.id IS 'Unique category identifier (BIGINT auto-increment)';
COMMENT ON COLUMN categories.name IS 'Human-readable category title (e.g., Wallets, Phones)';
COMMENT ON COLUMN categories.slug IS 'URL-friendly unique slug identifier';
COMMENT ON COLUMN categories.icon_name IS 'Icon identifier/name for frontend presentation';
COMMENT ON COLUMN categories.description IS 'Brief description explaining what belongs in this category';
COMMENT ON COLUMN categories.is_active IS 'Flag indicating if this category is available for item reporting';
COMMENT ON COLUMN categories.display_order IS 'Ordering sequence for displaying categories in UI';

CREATE INDEX idx_categories_slug ON categories(slug);
CREATE INDEX idx_categories_display_order ON categories(display_order);
CREATE INDEX idx_categories_is_active ON categories(is_active);

-- Seed Categories
INSERT INTO categories (name, slug, icon_name, description, display_order) VALUES
('Wallets & Purses', 'wallets-purses', 'wallet', 'Wallets, coin purses, card holders, and money clips', 1),
('Identity & Cards', 'identity-cards', 'id-card', 'National ID (NID), student IDs, driving licenses, and passports', 2),
('Phones & Tablets', 'phones-tablets', 'smartphone', 'Smartphones, iPhones, tablets, iPads, and smartwatches', 3),
('Laptops & Electronics', 'laptops-electronics', 'laptop', 'Laptops, chargers, headphones, power banks, and portable gadgets', 4),
('Bags & Luggage', 'bags-luggage', 'backpack', 'Backpacks, duffel bags, messenger bags, suitcases, and handbags', 5),
('Keys & Keychains', 'keys-keychains', 'key', 'House keys, motorcycle keys, car keys, and keychains', 6),
('Jewelry & Watches', 'jewelry-watches', 'watch', 'Wristwatches, rings, necklaces, earrings, and fine ornaments', 7),
('Books & Documents', 'books-documents', 'book', 'Academic textbooks, notes, certificates, diaries, and document folders', 8),
('Clothing & Eyewear', 'clothing-eyewear', 'glasses', 'Jackets, prescription glasses, sunglasses, hats, and footwear', 9),
('Other & Miscellaneous', 'other-miscellaneous', 'box', 'Other personal items not specifically classified above', 10);

-- ---------------------------------------------------------------------
-- 2. Table: locations
-- Description: Geographic registry of Bangladesh divisions, districts, and thanas/upazilas
-- ---------------------------------------------------------------------
CREATE TABLE locations (
    id BIGSERIAL PRIMARY KEY,
    division VARCHAR(100) NOT NULL,
    district VARCHAR(100) NOT NULL,
    thana VARCHAR(100) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_locations_district_thana UNIQUE (district, thana)
);

COMMENT ON TABLE locations IS 'Official Bangladesh administrative divisions, districts, and thana/upazila registry';
COMMENT ON COLUMN locations.id IS 'Unique location record identifier';
COMMENT ON COLUMN locations.division IS 'Administrative division (e.g., Dhaka, Chattogram, Rajshahi)';
COMMENT ON COLUMN locations.district IS 'Administrative district / zila (e.g., Dhaka, Gazipur, Cumilla)';
COMMENT ON COLUMN locations.thana IS 'Police station / upazila (e.g., Dhanmondi, Gulshan, Mirpur)';
COMMENT ON COLUMN locations.is_active IS 'Flag indicating if location is active for item placement';

CREATE INDEX idx_locations_division ON locations(division);
CREATE INDEX idx_locations_district ON locations(district);
CREATE INDEX idx_locations_thana ON locations(thana);
CREATE INDEX idx_locations_is_active ON locations(is_active);

-- ---------------------------------------------------------------------
-- Seed Bangladesh Divisions, 64 Districts and Upazilas / Thanas
-- ---------------------------------------------------------------------

-- DHAKA DIVISION
INSERT INTO locations (division, district, thana) VALUES
-- Dhaka District (Metropolitan Thanas & Upazilas)
('Dhaka', 'Dhaka', 'Adabor'),
('Dhaka', 'Dhaka', 'Badda'),
('Dhaka', 'Dhaka', 'Bangshal'),
('Dhaka', 'Dhaka', 'Bimanbandar'),
('Dhaka', 'Dhaka', 'Cantonment'),
('Dhaka', 'Dhaka', 'Chawkbazar'),
('Dhaka', 'Dhaka', 'Dakshinkhan'),
('Dhaka', 'Dhaka', 'Darus Salam'),
('Dhaka', 'Dhaka', 'Demra'),
('Dhaka', 'Dhaka', 'Dhanmondi'),
('Dhaka', 'Dhaka', 'Dhamrai'),
('Dhaka', 'Dhaka', 'Dohar'),
('Dhaka', 'Dhaka', 'Gendaria'),
('Dhaka', 'Dhaka', 'Gulshan'),
('Dhaka', 'Dhaka', 'Hazaribagh'),
('Dhaka', 'Dhaka', 'Jatrabari'),
('Dhaka', 'Dhaka', 'Kadamtali'),
('Dhaka', 'Dhaka', 'Kafrul'),
('Dhaka', 'Dhaka', 'Kalabagan'),
('Dhaka', 'Dhaka', 'Kamrangirchar'),
('Dhaka', 'Dhaka', 'Keraniganj'),
('Dhaka', 'Dhaka', 'Khilgaon'),
('Dhaka', 'Dhaka', 'Khilkhet'),
('Dhaka', 'Dhaka', 'Kotwali'),
('Dhaka', 'Dhaka', 'Lalbagh'),
('Dhaka', 'Dhaka', 'Mirpur'),
('Dhaka', 'Dhaka', 'Mohammadpur'),
('Dhaka', 'Dhaka', 'Motijheel'),
('Dhaka', 'Dhaka', 'Nawabganj'),
('Dhaka', 'Dhaka', 'New Market'),
('Dhaka', 'Dhaka', 'Pallabi'),
('Dhaka', 'Dhaka', 'Paltan'),
('Dhaka', 'Dhaka', 'Panthapath'),
('Dhaka', 'Dhaka', 'Ramna'),
('Dhaka', 'Dhaka', 'Rampura'),
('Dhaka', 'Dhaka', 'Sabujbagh'),
('Dhaka', 'Dhaka', 'Savar'),
('Dhaka', 'Dhaka', 'Shah Ali'),
('Dhaka', 'Dhaka', 'Shahbagh'),
('Dhaka', 'Dhaka', 'Sher-e-Bangla Nagar'),
('Dhaka', 'Dhaka', 'Shyampur'),
('Dhaka', 'Dhaka', 'Sutrapur'),
('Dhaka', 'Dhaka', 'Tejgaon'),
('Dhaka', 'Dhaka', 'Tejgaon Industrial Area'),
('Dhaka', 'Dhaka', 'Turag'),
('Dhaka', 'Dhaka', 'Uttar Khan'),
('Dhaka', 'Dhaka', 'Uttara East'),
('Dhaka', 'Dhaka', 'Uttara West'),
('Dhaka', 'Dhaka', 'Vatara'),
('Dhaka', 'Dhaka', 'Wari'),

-- Gazipur
('Dhaka', 'Gazipur', 'Gazipur Sadar'),
('Dhaka', 'Gazipur', 'Kaliakair'),
('Dhaka', 'Gazipur', 'Kaliganj'),
('Dhaka', 'Gazipur', 'Kapasia'),
('Dhaka', 'Gazipur', 'Sreepur'),
('Dhaka', 'Gazipur', 'Tongi'),

-- Narayanganj
('Dhaka', 'Narayanganj', 'Araihazar'),
('Dhaka', 'Narayanganj', 'Bandar'),
('Dhaka', 'Narayanganj', 'Narayanganj Sadar'),
('Dhaka', 'Narayanganj', 'Rupganj'),
('Dhaka', 'Narayanganj', 'Sonargaon'),
('Dhaka', 'Narayanganj', 'Fatullah'),
('Dhaka', 'Narayanganj', 'Siddhirganj'),

-- Narsingdi
('Dhaka', 'Narsingdi', 'Belabo'),
('Dhaka', 'Narsingdi', 'Monohardi'),
('Dhaka', 'Narsingdi', 'Narsingdi Sadar'),
('Dhaka', 'Narsingdi', 'Palash'),
('Dhaka', 'Narsingdi', 'Raipura'),
('Dhaka', 'Narsingdi', 'Shibpur'),

-- Tangail
('Dhaka', 'Tangail', 'Basail'),
('Dhaka', 'Tangail', 'Bhuapur'),
('Dhaka', 'Tangail', 'Delduar'),
('Dhaka', 'Tangail', 'Dhanbari'),
('Dhaka', 'Tangail', 'Ghatail'),
('Dhaka', 'Tangail', 'Gopalpur'),
('Dhaka', 'Tangail', 'Kalihati'),
('Dhaka', 'Tangail', 'Madhupur'),
('Dhaka', 'Tangail', 'Mirzapur'),
('Dhaka', 'Tangail', 'Nagarpur'),
('Dhaka', 'Tangail', 'Sakhipur'),
('Dhaka', 'Tangail', 'Tangail Sadar'),

-- Kishoreganj
('Dhaka', 'Kishoreganj', 'Austagram'),
('Dhaka', 'Kishoreganj', 'Bajitpur'),
('Dhaka', 'Kishoreganj', 'Bhairab'),
('Dhaka', 'Kishoreganj', 'Hossainpur'),
('Dhaka', 'Kishoreganj', 'Itna'),
('Dhaka', 'Kishoreganj', 'Karimganj'),
('Dhaka', 'Kishoreganj', 'Katiadi'),
('Dhaka', 'Kishoreganj', 'Kishoreganj Sadar'),
('Dhaka', 'Kishoreganj', 'Kuliarchar'),
('Dhaka', 'Kishoreganj', 'Mithamain'),
('Dhaka', 'Kishoreganj', 'Nikli'),
('Dhaka', 'Kishoreganj', 'Pakundia'),
('Dhaka', 'Kishoreganj', 'Tarail'),

-- Manikganj
('Dhaka', 'Manikganj', 'Daulatpur'),
('Dhaka', 'Manikganj', 'Ghior'),
('Dhaka', 'Manikganj', 'Harirampur'),
('Dhaka', 'Manikganj', 'Manikganj Sadar'),
('Dhaka', 'Manikganj', 'Saturia'),
('Dhaka', 'Manikganj', 'Shivalaya'),
('Dhaka', 'Manikganj', 'Singair'),

-- Munshiganj
('Dhaka', 'Munshiganj', 'Gazaria'),
('Dhaka', 'Munshiganj', 'Lohajang'),
('Dhaka', 'Munshiganj', 'Munshiganj Sadar'),
('Dhaka', 'Munshiganj', 'Sirajdikhan'),
('Dhaka', 'Munshiganj', 'Sreenagar'),
('Dhaka', 'Munshiganj', 'Tongibari'),

-- Faridpur
('Dhaka', 'Faridpur', 'Alfadanga'),
('Dhaka', 'Faridpur', 'Bhanga'),
('Dhaka', 'Faridpur', 'Boalmari'),
('Dhaka', 'Faridpur', 'Charbhadrasan'),
('Dhaka', 'Faridpur', 'Faridpur Sadar'),
('Dhaka', 'Faridpur', 'Madhukhali'),
('Dhaka', 'Faridpur', 'Nagarkanda'),
('Dhaka', 'Faridpur', 'Sadarpur'),
('Dhaka', 'Faridpur', 'Saltha'),

-- Gopalganj
('Dhaka', 'Gopalganj', 'Gopalganj Sadar'),
('Dhaka', 'Gopalganj', 'Kashiani'),
('Dhaka', 'Gopalganj', 'Kotalipara'),
('Dhaka', 'Gopalganj', 'Muksudpur'),
('Dhaka', 'Gopalganj', 'Tungipara'),

-- Madaripur
('Dhaka', 'Madaripur', 'Barhamganj'),
('Dhaka', 'Madaripur', 'Kalkini'),
('Dhaka', 'Madaripur', 'Madaripur Sadar'),
('Dhaka', 'Madaripur', 'Rajoir'),
('Dhaka', 'Madaripur', 'Shibchar'),

-- Rajbari
('Dhaka', 'Rajbari', 'Baliakandi'),
('Dhaka', 'Rajbari', 'Goalandaghat'),
('Dhaka', 'Rajbari', 'Kalukhali'),
('Dhaka', 'Rajbari', 'Pangsha'),
('Dhaka', 'Rajbari', 'Rajbari Sadar'),

-- Shariatpur
('Dhaka', 'Shariatpur', 'Bhedarganj'),
('Dhaka', 'Shariatpur', 'Damudya'),
('Dhaka', 'Shariatpur', 'Gosairhat'),
('Dhaka', 'Shariatpur', 'Naria'),
('Dhaka', 'Shariatpur', 'Shariatpur Sadar'),
('Dhaka', 'Shariatpur', 'Zajira'),

-- CHATTOGRAM DIVISION
-- Chattogram District
('Chattogram', 'Chattogram', 'Anwara'),
('Chattogram', 'Chattogram', 'Bakalia'),
('Chattogram', 'Chattogram', 'Bandar (Chattogram)'),
('Chattogram', 'Chattogram', 'Banshkhali'),
('Chattogram', 'Chattogram', 'Bayazid Bostami'),
('Chattogram', 'Chattogram', 'Boalkhali'),
('Chattogram', 'Chattogram', 'Chandgaon'),
('Chattogram', 'Chattogram', 'Chandanpura'),
('Chattogram', 'Chattogram', 'Double Mooring'),
('Chattogram', 'Chattogram', 'Halishahar'),
('Chattogram', 'Chattogram', 'Hathazari'),
('Chattogram', 'Chattogram', 'Kotwali (Chattogram)'),
('Chattogram', 'Chattogram', 'Khulshi'),
('Chattogram', 'Chattogram', 'Lohagara'),
('Chattogram', 'Chattogram', 'Mirsharai'),
('Chattogram', 'Chattogram', 'Pahartali'),
('Chattogram', 'Chattogram', 'Panchlaish'),
('Chattogram', 'Chattogram', 'Patenga'),
('Chattogram', 'Chattogram', 'Patiya'),
('Chattogram', 'Chattogram', 'Rangunia'),
('Chattogram', 'Chattogram', 'Raozan'),
('Chattogram', 'Chattogram', 'Sandwip'),
('Chattogram', 'Chattogram', 'Satkania'),
('Chattogram', 'Chattogram', 'Sitakunda'),

-- Cox''s Bazar
('Chattogram', 'Cox''s Bazar', 'Chakaria'),
('Chattogram', 'Cox''s Bazar', 'Cox''s Bazar Sadar'),
('Chattogram', 'Cox''s Bazar', 'Kutubdia'),
('Chattogram', 'Cox''s Bazar', 'Maheshkhali'),
('Chattogram', 'Cox''s Bazar', 'Pekua'),
('Chattogram', 'Cox''s Bazar', 'Ramu'),
('Chattogram', 'Cox''s Bazar', 'Teknaf'),
('Chattogram', 'Cox''s Bazar', 'Ukhia'),

-- Cumilla
('Chattogram', 'Cumilla', 'Barura'),
('Chattogram', 'Cumilla', 'Brahmanpara'),
('Chattogram', 'Cumilla', 'Burichang'),
('Chattogram', 'Cumilla', 'Chandina'),
('Chattogram', 'Cumilla', 'Chauddagram'),
('Chattogram', 'Cumilla', 'Cumilla Sadar'),
('Chattogram', 'Cumilla', 'Cumilla Sadar Dakshin'),
('Chattogram', 'Cumilla', 'Daudkandi'),
('Chattogram', 'Cumilla', 'Debidwar'),
('Chattogram', 'Cumilla', 'Homna'),
('Chattogram', 'Cumilla', 'Laksam'),
('Chattogram', 'Cumilla', 'Lalmai'),
('Chattogram', 'Cumilla', 'Meghna'),
('Chattogram', 'Cumilla', 'Monohargonj'),
('Chattogram', 'Cumilla', 'Muradnagar'),
('Chattogram', 'Cumilla', 'Nangalkot'),
('Chattogram', 'Cumilla', 'Titas'),

-- Feni
('Chattogram', 'Feni', 'Chhagalnaiya'),
('Chattogram', 'Feni', 'Daganbhuiyan'),
('Chattogram', 'Feni', 'Feni Sadar'),
('Chattogram', 'Feni', 'Fulgazi'),
('Chattogram', 'Feni', 'Parshuram'),
('Chattogram', 'Feni', 'Sonagazi'),

-- Brahmanbaria
('Chattogram', 'Brahmanbaria', 'Akhaura'),
('Chattogram', 'Brahmanbaria', 'Ashuganj'),
('Chattogram', 'Brahmanbaria', 'Bancharampur'),
('Chattogram', 'Brahmanbaria', 'Bijoynagar'),
('Chattogram', 'Brahmanbaria', 'Brahmanbaria Sadar'),
('Chattogram', 'Brahmanbaria', 'Kasba'),
('Chattogram', 'Brahmanbaria', 'Nabinagar'),
('Chattogram', 'Brahmanbaria', 'Nasirnagar'),
('Chattogram', 'Brahmanbaria', 'Sarail'),

-- Chandpur
('Chattogram', 'Chandpur', 'Chandpur Sadar'),
('Chattogram', 'Chandpur', 'Faridganj'),
('Chattogram', 'Chandpur', 'Haimchar'),
('Chattogram', 'Chandpur', 'Haziganj'),
('Chattogram', 'Chandpur', 'Kachua'),
('Chattogram', 'Chandpur', 'Matlab Dakshin'),
('Chattogram', 'Chandpur', 'Matlab Uttar'),
('Chattogram', 'Chandpur', 'Shahrasti'),

-- Noakhali
('Chattogram', 'Noakhali', 'Begumganj'),
('Chattogram', 'Noakhali', 'Chatkhil'),
('Chattogram', 'Noakhali', 'Companiganj'),
('Chattogram', 'Noakhali', 'Hatiya'),
('Chattogram', 'Noakhali', 'Kabirhat'),
('Chattogram', 'Noakhali', 'Noakhali Sadar'),
('Chattogram', 'Noakhali', 'Senbagh'),
('Chattogram', 'Noakhali', 'Sonaimuri'),
('Chattogram', 'Noakhali', 'Subarnachar'),

-- Lakshmipur
('Chattogram', 'Lakshmipur', 'Kamalnagar'),
('Chattogram', 'Lakshmipur', 'Lakshmipur Sadar'),
('Chattogram', 'Lakshmipur', 'Raipur'),
('Chattogram', 'Lakshmipur', 'Ramganj'),
('Chattogram', 'Lakshmipur', 'Ramgati'),

-- Khagrachhari
('Chattogram', 'Khagrachhari', 'Dighinala'),
('Chattogram', 'Khagrachhari', 'Guimara'),
('Chattogram', 'Khagrachhari', 'Khagrachhari Sadar'),
('Chattogram', 'Khagrachhari', 'Lakshmichhari'),
('Chattogram', 'Khagrachhari', 'Mahalchhari'),
('Chattogram', 'Khagrachhari', 'Manikchhari'),
('Chattogram', 'Khagrachhari', 'Matiranga'),
('Chattogram', 'Khagrachhari', 'Panchhari'),
('Chattogram', 'Khagrachhari', 'Ramgarh'),

-- Rangamati
('Chattogram', 'Rangamati', 'Baghaichhari'),
('Chattogram', 'Rangamati', 'Barkal'),
('Chattogram', 'Rangamati', 'Belaichhari'),
('Chattogram', 'Rangamati', 'Juraichhari'),
('Chattogram', 'Rangamati', 'Kaptai'),
('Chattogram', 'Rangamati', 'Kawkhali'),
('Chattogram', 'Rangamati', 'Langadu'),
('Chattogram', 'Rangamati', 'Naniarchar'),
('Chattogram', 'Rangamati', 'Rajasthali'),
('Chattogram', 'Rangamati', 'Rangamati Sadar'),

-- Bandarban
('Chattogram', 'Bandarban', 'Ali Kadam'),
('Chattogram', 'Bandarban', 'Bandarban Sadar'),
('Chattogram', 'Bandarban', 'Lama'),
('Chattogram', 'Bandarban', 'Naikhongchhari'),
('Chattogram', 'Bandarban', 'Rowangchhari'),
('Chattogram', 'Bandarban', 'Ruma'),
('Chattogram', 'Bandarban', 'Thanchi'),

-- RAJSHAHI DIVISION
-- Rajshahi
('Rajshahi', 'Rajshahi', 'Bagha'),
('Rajshahi', 'Rajshahi', 'Bagmara'),
('Rajshahi', 'Rajshahi', 'Boalia'),
('Rajshahi', 'Rajshahi', 'Charghat'),
('Rajshahi', 'Rajshahi', 'Durgapur (Rajshahi)'),
('Rajshahi', 'Rajshahi', 'Godagari'),
('Rajshahi', 'Rajshahi', 'Matihar'),
('Rajshahi', 'Rajshahi', 'Mohonpur'),
('Rajshahi', 'Rajshahi', 'Paba'),
('Rajshahi', 'Rajshahi', 'Puthia'),
('Rajshahi', 'Rajshahi', 'Rajpara'),
('Rajshahi', 'Rajshahi', 'Shah Makhdum'),
('Rajshahi', 'Rajshahi', 'Tanore'),

-- Bogura
('Rajshahi', 'Bogura', 'Adamdighi'),
('Rajshahi', 'Bogura', 'Bogura Sadar'),
('Rajshahi', 'Bogura', 'Dhunat'),
('Rajshahi', 'Bogura', 'Dhupchanchia'),
('Rajshahi', 'Bogura', 'Gabtali'),
('Rajshahi', 'Bogura', 'Kahaloo'),
('Rajshahi', 'Bogura', 'Nandigram'),
('Rajshahi', 'Bogura', 'Sariakandi'),
('Rajshahi', 'Bogura', 'Shajahanpur'),
('Rajshahi', 'Bogura', 'Sherpur'),
('Rajshahi', 'Bogura', 'Shibganj (Bogura)'),
('Rajshahi', 'Bogura', 'Sonatala'),

-- Pabna
('Rajshahi', 'Pabna', 'Atgharia'),
('Rajshahi', 'Pabna', 'Bera'),
('Rajshahi', 'Pabna', 'Bhangura'),
('Rajshahi', 'Pabna', 'Chatmohar'),
('Rajshahi', 'Pabna', 'Faridpur (Pabna)'),
('Rajshahi', 'Pabna', 'Ishwardi'),
('Rajshahi', 'Pabna', 'Pabna Sadar'),
('Rajshahi', 'Pabna', 'Santhia'),
('Rajshahi', 'Pabna', 'Sujanagar'),

-- Sirajganj
('Rajshahi', 'Sirajganj', 'Belkuchi'),
('Rajshahi', 'Sirajganj', 'Chauhali'),
('Rajshahi', 'Sirajganj', 'Kamarkhanda'),
('Rajshahi', 'Sirajganj', 'Kazipur'),
('Rajshahi', 'Sirajganj', 'Raiganj'),
('Rajshahi', 'Sirajganj', 'Shahjadpur'),
('Rajshahi', 'Sirajganj', 'Sirajganj Sadar'),
('Rajshahi', 'Sirajganj', 'Tarash'),
('Rajshahi', 'Sirajganj', 'Ullapara'),

-- Naogaon
('Rajshahi', 'Naogaon', 'Atrai'),
('Rajshahi', 'Naogaon', 'Badalgachhi'),
('Rajshahi', 'Naogaon', 'Dhamoirhat'),
('Rajshahi', 'Naogaon', 'Manda'),
('Rajshahi', 'Naogaon', 'Mohadevpur'),
('Rajshahi', 'Naogaon', 'Naogaon Sadar'),
('Rajshahi', 'Naogaon', 'Niamatpur'),
('Rajshahi', 'Naogaon', 'Patnitala'),
('Rajshahi', 'Naogaon', 'Porsha'),
('Rajshahi', 'Naogaon', 'Raninagar'),
('Rajshahi', 'Naogaon', 'Sapahar'),

-- Natore
('Rajshahi', 'Natore', 'Bagatipara'),
('Rajshahi', 'Natore', 'Baraigram'),
('Rajshahi', 'Natore', 'Gurudaspur'),
('Rajshahi', 'Natore', 'Lalpur'),
('Rajshahi', 'Natore', 'Naldanga'),
('Rajshahi', 'Natore', 'Natore Sadar'),
('Rajshahi', 'Natore', 'Singra'),

-- Chapainawabganj
('Rajshahi', 'Chapainawabganj', 'Bholahat'),
('Rajshahi', 'Chapainawabganj', 'Gomastapur'),
('Rajshahi', 'Chapainawabganj', 'Nachole'),
('Rajshahi', 'Chapainawabganj', 'Nawabganj Sadar'),
('Rajshahi', 'Chapainawabganj', 'Shibganj (Chapai)'),

-- Joypurhat
('Rajshahi', 'Joypurhat', 'Akkelpur'),
('Rajshahi', 'Joypurhat', 'Joypurhat Sadar'),
('Rajshahi', 'Joypurhat', 'Kalai'),
('Rajshahi', 'Joypurhat', 'Khetlal'),
('Rajshahi', 'Joypurhat', 'Panchbibi'),

-- KHULNA DIVISION
-- Khulna
('Khulna', 'Khulna', 'Batiaghata'),
('Khulna', 'Khulna', 'Dacope'),
('Khulna', 'Khulna', 'Daulatpur (Khulna)'),
('Khulna', 'Khulna', 'Dighalia'),
('Khulna', 'Khulna', 'Dumuria'),
('Khulna', 'Khulna', 'Khalishpur'),
('Khulna', 'Khulna', 'Khan Jahan Ali'),
('Khulna', 'Khulna', 'Khulna Sadar'),
('Khulna', 'Khulna', 'Koyra'),
('Khulna', 'Khulna', 'Paikgachha'),
('Khulna', 'Khulna', 'Phultala'),
('Khulna', 'Khulna', 'Rupsha'),
('Khulna', 'Khulna', 'Sonadanga'),
('Khulna', 'Khulna', 'Terokhada'),

-- Jashore
('Khulna', 'Jashore', 'Abhaynagar'),
('Khulna', 'Jashore', 'Bagherpara'),
('Khulna', 'Jashore', 'Chaugachha'),
('Khulna', 'Jashore', 'Jhikargachha'),
('Khulna', 'Jashore', 'Keshabpur'),
('Khulna', 'Jashore', 'Kotwali (Jashore)'),
('Khulna', 'Jashore', 'Manirampur'),
('Khulna', 'Jashore', 'Sharsha'),

-- Satkhira
('Khulna', 'Satkhira', 'Assasuni'),
('Khulna', 'Satkhira', 'Debhata'),
('Khulna', 'Satkhira', 'Kalaroa'),
('Khulna', 'Satkhira', 'Kaliganj (Satkhira)'),
('Khulna', 'Satkhira', 'Satkhira Sadar'),
('Khulna', 'Satkhira', 'Shyamnagar'),
('Khulna', 'Satkhira', 'Tala'),

-- Kushtia
('Khulna', 'Kushtia', 'Bheramara'),
('Khulna', 'Kushtia', 'Daulatpur (Kushtia)'),
('Khulna', 'Kushtia', 'Khoksa'),
('Khulna', 'Kushtia', 'Kumarkhali'),
('Khulna', 'Kushtia', 'Kushtia Sadar'),
('Khulna', 'Kushtia', 'Mirpur (Kushtia)'),

-- Bagerhat
('Khulna', 'Bagerhat', 'Bagerhat Sadar'),
('Khulna', 'Bagerhat', 'Chitalmari'),
('Khulna', 'Bagerhat', 'Fakirhat'),
('Khulna', 'Bagerhat', 'Kachua (Bagerhat)'),
('Khulna', 'Bagerhat', 'Mollahat'),
('Khulna', 'Bagerhat', 'Mongla'),
('Khulna', 'Bagerhat', 'Morrelganj'),
('Khulna', 'Bagerhat', 'Rampal'),
('Khulna', 'Bagerhat', 'Sarankhola'),

-- Jhenaidah
('Khulna', 'Jhenaidah', 'Harinakunda'),
('Khulna', 'Jhenaidah', 'Jhenaidah Sadar'),
('Khulna', 'Jhenaidah', 'Kaliganj (Jhenaidah)'),
('Khulna', 'Jhenaidah', 'Kotchandpur'),
('Khulna', 'Jhenaidah', 'Maheshpur'),
('Khulna', 'Jhenaidah', 'Shailkupa'),

-- Chuadanga
('Khulna', 'Chuadanga', 'Alamdanga'),
('Khulna', 'Chuadanga', 'Chuadanga Sadar'),
('Khulna', 'Chuadanga', 'Damurhuda'),
('Khulna', 'Chuadanga', 'Jibannagar'),

-- Magura
('Khulna', 'Magura', 'Magura Sadar'),
('Khulna', 'Magura', 'Mohammadpur (Magura)'),
('Khulna', 'Magura', 'Shalikha'),
('Khulna', 'Magura', 'Sreepur (Magura)'),

-- Meherpur
('Khulna', 'Meherpur', 'Gangni'),
('Khulna', 'Meherpur', 'Meherpur Sadar'),
('Khulna', 'Meherpur', 'Mujibnagar'),

-- Narail
('Khulna', 'Narail', 'Kalia'),
('Khulna', 'Narail', 'Lohagara (Narail)'),
('Khulna', 'Narail', 'Narail Sadar'),

-- BARISHAL DIVISION
-- Barishal
('Barishal', 'Barishal', 'Agailjhara'),
('Barishal', 'Barishal', 'Babuganj'),
('Barishal', 'Barishal', 'Bakerganj'),
('Barishal', 'Barishal', 'Banaripara'),
('Barishal', 'Barishal', 'Gaurnadi'),
('Barishal', 'Barishal', 'Hizla'),
('Barishal', 'Barishal', 'Barishal Sadar'),
('Barishal', 'Barishal', 'Mehendiganj'),
('Barishal', 'Barishal', 'Muladi'),
('Barishal', 'Barishal', 'Wazirpur'),

-- Bhola
('Barishal', 'Bhola', 'Bhola Sadar'),
('Barishal', 'Bhola', 'Burhanuddin'),
('Barishal', 'Bhola', 'Char Fasson'),
('Barishal', 'Bhola', 'Daulatkhan'),
('Barishal', 'Bhola', 'Lalmohan'),
('Barishal', 'Bhola', 'Manpura'),
('Barishal', 'Bhola', 'Tazumuddin'),

-- Patuakhali
('Barishal', 'Patuakhali', 'Bauphal'),
('Barishal', 'Patuakhali', 'Dashmina'),
('Barishal', 'Patuakhali', 'Dumki'),
('Barishal', 'Patuakhali', 'Galachipa'),
('Barishal', 'Patuakhali', 'Kalapara'),
('Barishal', 'Patuakhali', 'Mirzaganj'),
('Barishal', 'Patuakhali', 'Patuakhali Sadar'),
('Barishal', 'Patuakhali', 'Rangabali'),

-- Pirojpur
('Barishal', 'Pirojpur', 'Bhandaria'),
('Barishal', 'Pirojpur', 'Kawkhali (Pirojpur)'),
('Barishal', 'Pirojpur', 'Mathbaria'),
('Barishal', 'Pirojpur', 'Nazirpur'),
('Barishal', 'Pirojpur', 'Nesarabad (Swarupkati)'),
('Barishal', 'Pirojpur', 'Pirojpur Sadar'),
('Barishal', 'Pirojpur', 'Zianagar (Indurkani)'),

-- Barguna
('Barishal', 'Barguna', 'Amtali'),
('Barishal', 'Barguna', 'Bamna'),
('Barishal', 'Barguna', 'Barguna Sadar'),
('Barishal', 'Barguna', 'Betagi'),
('Barishal', 'Barguna', 'Patharghata'),
('Barishal', 'Barguna', 'Taltali'),

-- Jhalokati
('Barishal', 'Jhalokati', 'Jhalokati Sadar'),
('Barishal', 'Jhalokati', 'Kathalia'),
('Barishal', 'Jhalokati', 'Nalchity'),
('Barishal', 'Jhalokati', 'Rajapur'),

-- SYLHET DIVISION
-- Sylhet
('Sylhet', 'Sylhet', 'Balaganj'),
('Sylhet', 'Sylhet', 'Beanibazar'),
('Sylhet', 'Sylhet', 'Bishwanath'),
('Sylhet', 'Sylhet', 'Companiganj (Sylhet)'),
('Sylhet', 'Sylhet', 'Dakshin Surma'),
('Sylhet', 'Sylhet', 'Fenchuganj'),
('Sylhet', 'Sylhet', 'Golapganj'),
('Sylhet', 'Sylhet', 'Gowainghat'),
('Sylhet', 'Sylhet', 'Jaintiapur'),
('Sylhet', 'Sylhet', 'Kanaighat'),
('Sylhet', 'Sylhet', 'Kotwali (Sylhet)'),
('Sylhet', 'Sylhet', 'Osmani Nagar'),
('Sylhet', 'Sylhet', 'Shah Paran'),
('Sylhet', 'Sylhet', 'Zakiganj'),

-- Moulvibazar
('Sylhet', 'Moulvibazar', 'Barlekha'),
('Sylhet', 'Moulvibazar', 'Juri'),
('Sylhet', 'Moulvibazar', 'Kamalganj'),
('Sylhet', 'Moulvibazar', 'Kulaura'),
('Sylhet', 'Moulvibazar', 'Moulvibazar Sadar'),
('Sylhet', 'Moulvibazar', 'Rajnagar'),
('Sylhet', 'Moulvibazar', 'Sreemangal'),

-- Habiganj
('Sylhet', 'Habiganj', 'Ajmiriganj'),
('Sylhet', 'Habiganj', 'Bahubal'),
('Sylhet', 'Habiganj', 'Baniyachong'),
('Sylhet', 'Habiganj', 'Chunarughat'),
('Sylhet', 'Habiganj', 'Habiganj Sadar'),
('Sylhet', 'Habiganj', 'Lakhai'),
('Sylhet', 'Habiganj', 'Madhabpur'),
('Sylhet', 'Habiganj', 'Nabiganj'),
('Sylhet', 'Habiganj', 'Sayestaganj'),

-- Sunamganj
('Sylhet', 'Sunamganj', 'Bishwamvarpur'),
('Sylhet', 'Sunamganj', 'Chhatak'),
('Sylhet', 'Sunamganj', 'Dakshin Sunamganj (Shantiganj)'),
('Sylhet', 'Sunamganj', 'Derai'),
('Sylhet', 'Sunamganj', 'Dharampasha'),
('Sylhet', 'Sunamganj', 'Dowarabazar'),
('Sylhet', 'Sunamganj', 'Jagannathpur'),
('Sylhet', 'Sunamganj', 'Jamalganj'),
('Sylhet', 'Sunamganj', 'Sullah'),
('Sylhet', 'Sunamganj', 'Sunamganj Sadar'),
('Sylhet', 'Sunamganj', 'Tahirpur'),

-- RANGPUR DIVISION
-- Rangpur
('Rangpur', 'Rangpur', 'Badarganj'),
('Rangpur', 'Rangpur', 'Gangachhara'),
('Rangpur', 'Rangpur', 'Kaunia'),
('Rangpur', 'Rangpur', 'Mithapukur'),
('Rangpur', 'Rangpur', 'Pirgachha'),
('Rangpur', 'Rangpur', 'Pirganj (Rangpur)'),
('Rangpur', 'Rangpur', 'Rangpur Sadar'),
('Rangpur', 'Rangpur', 'Taraganj'),

-- Dinajpur
('Rangpur', 'Dinajpur', 'Birampur'),
('Rangpur', 'Dinajpur', 'Birganj'),
('Rangpur', 'Dinajpur', 'Biral'),
('Rangpur', 'Dinajpur', 'Bochaganj'),
('Rangpur', 'Dinajpur', 'Chirirbandar'),
('Rangpur', 'Dinajpur', 'Dinajpur Sadar'),
('Rangpur', 'Dinajpur', 'Ghoraghat'),
('Rangpur', 'Dinajpur', 'Hakimpur'),
('Rangpur', 'Dinajpur', 'Kaharole'),
('Rangpur', 'Dinajpur', 'Khansama'),
('Rangpur', 'Dinajpur', 'Nawabganj (Dinajpur)'),
('Rangpur', 'Dinajpur', 'Parbatipur'),
('Rangpur', 'Dinajpur', 'Phulbari (Dinajpur)'),

-- Gaibandha
('Rangpur', 'Gaibandha', 'Fulchhari'),
('Rangpur', 'Gaibandha', 'Gaibandha Sadar'),
('Rangpur', 'Gaibandha', 'Gobindaganj'),
('Rangpur', 'Gaibandha', 'Palashbari'),
('Rangpur', 'Gaibandha', 'Sadullapur'),
('Rangpur', 'Gaibandha', 'Saghata'),
('Rangpur', 'Gaibandha', 'Sundarganj'),

-- Kurigram
('Rangpur', 'Kurigram', 'Bhurungamari'),
('Rangpur', 'Kurigram', 'Char Rajibpur'),
('Rangpur', 'Kurigram', 'Chilmari'),
('Rangpur', 'Kurigram', 'Kurigram Sadar'),
('Rangpur', 'Kurigram', 'Nageshwari'),
('Rangpur', 'Kurigram', 'Phulbari (Kurigram)'),
('Rangpur', 'Kurigram', 'Rajarhat'),
('Rangpur', 'Kurigram', 'Raomari'),
('Rangpur', 'Kurigram', 'Ulipur'),

-- Lalmonirhat
('Rangpur', 'Lalmonirhat', 'Aditmari'),
('Rangpur', 'Lalmonirhat', 'Hatibandha'),
('Rangpur', 'Lalmonirhat', 'Kaliganj (Lalmonirhat)'),
('Rangpur', 'Lalmonirhat', 'Lalmonirhat Sadar'),
('Rangpur', 'Lalmonirhat', 'Patgram'),

-- Nilphamari
('Rangpur', 'Nilphamari', 'Dimla'),
('Rangpur', 'Nilphamari', 'Domar'),
('Rangpur', 'Nilphamari', 'Jaldhaka'),
('Rangpur', 'Nilphamari', 'Kishoreganj (Nilphamari)'),
('Rangpur', 'Nilphamari', 'Nilphamari Sadar'),
('Rangpur', 'Nilphamari', 'Saidpur'),

-- Panchagarh
('Rangpur', 'Panchagarh', 'Atwari'),
('Rangpur', 'Panchagarh', 'Boda'),
('Rangpur', 'Panchagarh', 'Debiganj'),
('Rangpur', 'Panchagarh', 'Panchagarh Sadar'),
('Rangpur', 'Panchagarh', 'Tetulia'),

-- Thakurgaon
('Rangpur', 'Thakurgaon', 'Baliadangi'),
('Rangpur', 'Thakurgaon', 'Haripur'),
('Rangpur', 'Thakurgaon', 'Pirganj (Thakurgaon)'),
('Rangpur', 'Thakurgaon', 'Ranisankhail'),
('Rangpur', 'Thakurgaon', 'Thakurgaon Sadar'),

-- MYMENSINGH DIVISION
-- Mymensingh
('Mymensingh', 'Mymensingh', 'Bhaluka'),
('Mymensingh', 'Mymensingh', 'Dhobaura'),
('Mymensingh', 'Mymensingh', 'Fulbaria'),
('Mymensingh', 'Mymensingh', 'Gaffargaon'),
('Mymensingh', 'Mymensingh', 'Gauripur'),
('Mymensingh', 'Mymensingh', 'Haluaghat'),
('Mymensingh', 'Mymensingh', 'Ishwarganj'),
('Mymensingh', 'Mymensingh', 'Kotwali (Mymensingh)'),
('Mymensingh', 'Mymensingh', 'Muktagachha'),
('Mymensingh', 'Mymensingh', 'Nandail'),
('Mymensingh', 'Mymensingh', 'Phulpur'),
('Mymensingh', 'Mymensingh', 'Trishal'),
('Mymensingh', 'Mymensingh', 'Tara Khanda'),

-- Jamalpur
('Mymensingh', 'Jamalpur', 'Bakshiganj'),
('Mymensingh', 'Jamalpur', 'Dewanganj'),
('Mymensingh', 'Jamalpur', 'Islampur'),
('Mymensingh', 'Jamalpur', 'Jamalpur Sadar'),
('Mymensingh', 'Jamalpur', 'Madarganj'),
('Mymensingh', 'Jamalpur', 'Melandaha'),
('Mymensingh', 'Jamalpur', 'Sarishabari'),

-- Netrokona
('Mymensingh', 'Netrokona', 'Atpara'),
('Mymensingh', 'Netrokona', 'Barhatta'),
('Mymensingh', 'Netrokona', 'Durgapur (Netrokona)'),
('Mymensingh', 'Netrokona', 'Kalmakanda'),
('Mymensingh', 'Netrokona', 'Kendua'),
('Mymensingh', 'Netrokona', 'Madan'),
('Mymensingh', 'Netrokona', 'Mohanganj'),
('Mymensingh', 'Netrokona', 'Netrokona Sadar'),
('Mymensingh', 'Netrokona', 'Purbadhala'),
('Mymensingh', 'Netrokona', 'Khaliajuri'),

-- Sherpur
('Mymensingh', 'Sherpur', 'Jhenaigati'),
('Mymensingh', 'Sherpur', 'Nakla'),
('Mymensingh', 'Sherpur', 'Nalitabari'),
('Mymensingh', 'Sherpur', 'Sherpur Sadar'),
('Mymensingh', 'Sherpur', 'Sreebardi');
