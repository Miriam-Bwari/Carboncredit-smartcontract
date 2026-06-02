# backend/create_tables.py


from database.connection import engine, Base
from database import models

def create_all_tables():
    print("Creating tables in MySQL...")

    try:
        Base.metadata.create_all(bind=engine)

        print("\nSUCCESS! Tables created:")
        print(" - farmers")
        print(" - farms")
        print(" - carbon_records")
        print(" - payments")

        print("\nOpen MySQL Workbench to confirm.")

    except Exception as e:
        print("\nERROR:")
        print(str(e))

if __name__ == "__main__":
    create_all_tables()