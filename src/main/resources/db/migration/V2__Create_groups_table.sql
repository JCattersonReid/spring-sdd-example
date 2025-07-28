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
    CONSTRAINT fk_groups_admin_id FOREIGN KEY (admin_id) REFERENCES users(id)
);

CREATE INDEX idx_groups_name ON groups(name);
CREATE INDEX idx_groups_status ON groups(status);
CREATE INDEX idx_groups_admin_id ON groups(admin_id);
CREATE UNIQUE INDEX idx_groups_name_status ON groups(name) WHERE status = 'ACTIVE';