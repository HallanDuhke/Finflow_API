CREATE TABLE accounts (
    id SERIAL PRIMARY KEY,
    client_id INT NOT NULL REFERENCES clients(id),
    account_number VARCHAR(50) NOT NULL,
    account_type VARCHAR(50),
    balance NUMERIC(18,2) DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);