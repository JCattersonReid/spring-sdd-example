CREATE TABLE groups (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(240) NOT NULL,
    description VARCHAR(500) NOT NULL,
    self_join BOOLEAN NOT NULL DEFAULT false,
    self_leave BOOLEAN NOT NULL DEFAULT false,
    admin_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT fk_groups_admin_id FOREIGN KEY (admin_id) REFERENCES users(id),
    CONSTRAINT uq_groups_name_active UNIQUE (name) DEFERRABLE INITIALLY DEFERRED
);

CREATE INDEX idx_groups_name ON groups(name);
CREATE INDEX idx_groups_status ON groups(status);
CREATE INDEX idx_groups_admin_id ON groups(admin_id);
CREATE INDEX idx_groups_name_status ON groups(name, status);

INSERT INTO groups (name, description, self_join, self_leave, admin_id, status) 
SELECT 
    'Default Group',
    'A default group for demonstration purposes',
    true,
    true,
    u.id,
    'ACTIVE'
FROM users u
WHERE u.username = 'john_doe'
LIMIT 1;