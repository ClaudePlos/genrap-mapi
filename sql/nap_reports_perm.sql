create table NAP_REPORTS_PERM
(
    PERM_ID NUMBER
        constraint NAP_REPORTS_PERM_PK
            unique,
    PERM_RAP_ID NUMBER,
    PERM_USER_ID NUMBER,
    PERM_USERNAME VARCHAR2(200)
)
    /



