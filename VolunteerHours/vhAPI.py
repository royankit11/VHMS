import pyodbc
from flask import Flask, request, jsonify
from flask_restful import Resource, Api


app = Flask(__name__)
objapi = Api(app)

class getUser(Resource):
    def get(self, strUsername, strPassword, blEditProfile):
        DBfile = "C:\\HOME\\Rik\\Projects\\Python\\VolunteerHours\\vhDatabase.accdb"

        conn = pyodbc.connect('Driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ='+DBfile)
        cur = conn.cursor()

        SQL = "SELECT A.Username, A.Password, A.First_Name, A.Last_Name, A.Grade, A.StudentNum, A.User_ID, A.Cat_ID, B.Category"
        SQL = SQL + " FROM User_Data A INNER JOIN Award_Category B ON A.Cat_ID = B.Cat_ID WHERE UCase(Username) = '" + strUsername.upper() + "'"
        cur.execute(SQL)
        users = cur.fetchall()
        cur.close()
        conn.close()

        userData = {}

        if users:
            if blEditProfile == "true":
                userData["Username"] = strUsername
                userData["Password"] = users[0][1]
                userData["First name"] = users[0][2]
                userData["Last name"] = users[0][3]
                userData["Grade"] = users[0][4]
                userData["Student Num"] = users[0][5]
                userData["User_ID"] = users[0][6]
                userData["Category_ID"] = users[0][7]
                userData["Award Category"] = users[0][8] 
                userData["Error"] = ""
                return userData
            else:
                if strPassword == str(users[0][1]):        
                    userData["Username"] = strUsername
                    userData["First name"] = users[0][2]
                    userData["Last name"] = users[0][3]
                    userData["Grade"] = users[0][4]
                    userData["Student Num"] = users[0][5]
                    userData["User_ID"] = users[0][6]
                    userData["Award Category"] = users[0][8] 
                    userData["Error"] = ""
                    return userData

                else:
                    userData["Username"] = ""
                    userData["User ID"] = ""
                    userData["First name"] = "" 
                    userData["Error"] = "Invalid Password"
                    return userData
        else:
            userData["Username"] = ""
            userData["User ID"] = ""
            userData["First name"] = "" 
            userData["Error"] = "Invalid Username"
            return userData

class Register(Resource):
    def get(self, strFirstName, strLastName, strUsername, strPassword, strGrade, strStudentNum, strCatID):
        
        DBfile = "C:\\HOME\\Rik\\Projects\\Python\\VolunteerHours\\vhDatabase.accdb"

        conn = pyodbc.connect('Driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ='+DBfile)
        cur = conn.cursor()
        message = {}

        SQL = "SELECT Username FROM User_Data WHERE UCase(Username) = '" + strUsername.upper() + "'"
        cur.execute(SQL)
        users = cur.fetchall()
        cur.close()
        conn.close()

        if users:
            message["Message"] = "ERROR"
            return jsonify(message)
        
        else:
            conn2 = pyodbc.connect('Driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ='+DBfile)
            cur2 = conn2.cursor()
            
            SQL = "INSERT INTO User_Data (First_Name, Last_Name, Username, Password, Grade, StudentNum, Cat_ID)"
            SQL = SQL + "VALUES ('" + strFirstName + "' ,'" + strLastName + "' ,'" + strUsername + "' ,'" 
            SQL = SQL + strPassword + "'," + strGrade + "," + strStudentNum + "," + strCatID + ")"
            cur2.execute(SQL)
            conn2.commit()
            cur2.close()
            conn2.close()

            message["Message"] = "SUCCESS"
            
            return jsonify(message)

class updateProfile(Resource):
    def get(self, strFirstName, strLastName, strUsername, strPassword, strGrade, strStudentNum, strCatID, strUserID):
        
        DBfile = "C:\\HOME\\Rik\\Projects\\Python\\VolunteerHours\\vhDatabase.accdb"

        conn = pyodbc.connect('Driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ='+DBfile)
        cur = conn.cursor()
        message = {}

        SQL = "SELECT Username FROM User_Data WHERE UCase(Username) = '" + strUsername.upper() + "' AND User_ID <> " + strUserID
        cur.execute(SQL)
        users = cur.fetchall()
        cur.close()
        conn.close()

        if users:
            message["Message"] = "ERROR"
            return jsonify(message)
        else:                    
            SQL = "UPDATE User_Data SET First_Name = '" + strFirstName + "', Last_Name = '" + strLastName + "', Username = '" + strUsername + "',"
            SQL = SQL + " Password = '" + strPassword + "', Grade = " + strGrade + ", StudentNum = " + strStudentNum + ", Cat_ID = " + strCatID
            SQL = SQL + " WHERE User_ID = " + strUserID

        conn2 = pyodbc.connect('Driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ='+DBfile)
        cur2 = conn2.cursor()
        message2 = {}

        
        cur2.execute(SQL)
        conn2.commit()
        cur2.close()
        conn2.close()


        message2["Message"] = "SUCCESS"
        
        return jsonify(message2)
        
class getAwards(Resource):
    def get(self):
        DBfile = "C:\\HOME\\Rik\\Projects\\Python\\VolunteerHours\\vhDatabase.accdb"

        conn = pyodbc.connect('Driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ='+DBfile)
        cur = conn.cursor()

        SQL = "SELECT Cat_ID, Category FROM Award_Category"
        cur.execute(SQL)
        award_recs = cur.fetchall()
        dict_award_recs = dict(award_recs)
        
        cur.close()
        conn.close()

        awards = {}
        awards["categories"] = dict_award_recs
        
        return jsonify(awards)

class inputHours(Resource):
    def get(self, userID, volMonth, volDay, volYear, volHours, strLocation):
        DBfile = "C:\\HOME\\Rik\\Projects\\Python\\VolunteerHours\\vhDatabase.accdb"
        conn = pyodbc.connect('Driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ='+DBfile)

        volDate = volMonth + "/" + volDay + "/" + volYear
        print(volDate)
        
        cur = conn.cursor()
        SQL = "INSERT INTO Volunteer_Hours (User_ID, Vol_Date, Vol_Hours, Location)"
        SQL = SQL + "VALUES (" + userID + ",'" + volDate + "'," + volHours + ", '" + strLocation + "')"
        
        cur.execute(SQL)
        conn.commit()
        cur.close()
        conn.close()

        message = {}
        message["Message"] = "SUCCESS"
        
        return jsonify(message)

            
class viewHours(Resource):
    def get(self, monthStart, dayStart, yearStart, monthEnd, dayEnd, yearEnd, userID):
        DBfile = "C:\\HOME\\Rik\\Projects\\Python\\VolunteerHours\\vhDatabase.accdb"
        conn = pyodbc.connect('Driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ='+DBfile)
        cur = conn.cursor()

        strStartDate = monthStart + "/" + dayStart + "/" + yearStart
        strEndDate = monthEnd + "/" + dayEnd + "/" + yearEnd
        
        SQL = "SELECT FORMAT(A.Vol_Date,'MM/dd/yyyy'), A.Vol_Hours, A.Location, A.Vol_ID, C.Category, C.Cat_Hours FROM "
        SQL = SQL + "((Volunteer_Hours A INNER JOIN User_Data B ON A.User_ID = B.User_ID) INNER JOIN Award_Category C ON B.Cat_ID = C.Cat_ID)"
        SQL = SQL + " WHERE A.User_ID = " + userID + " AND A.Vol_Date BETWEEN #" + strStartDate + "# AND #" + strEndDate + "#"
        SQL = SQL + " ORDER BY A.Vol_Date"

        cur.execute(SQL)
        allData = cur.fetchall()

        cur.close()
        conn.close()

        conn2 = pyodbc.connect('Driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ='+DBfile)
        cur2 = conn2.cursor()

        SQL2 = "SELECT SUM(Vol_Hours) as Total_Hours FROM Volunteer_Hours WHERE User_ID = " + userID

        cur2.execute(SQL2)
        totalHours = cur2.fetchall()

        cur2.close()
        conn2.close()

        allData_arr = []
        

        if allData:
            for i in range (0, len(allData)):
                allData_dict = {}
                allData_dict["Date"] = allData[i][0]
                allData_dict["Hours"] = allData[i][1]
                allData_dict["Location"] = allData[i][2]
                allData_dict["ID"] = allData[i][3]
                allData_dict["Category"] = allData[i][4]
                allData_dict["HoursAward"] = allData[i][5]
                allData_dict["TotalHours"] = totalHours[0][0]
                allData_dict["Error"] = ""
                
                
                allData_arr.append(allData_dict)
                
                
        else:
            allData_dict = {}
            allData_dict["Date"] = ""
            allData_dict["Hours"] = ""
            allData_dict["Location"] = ""
            allData_dict["ID"] = ""
            allData_dict["Category"] = ""
            allData_dict["HoursAward"] = ""
            allData_dict["TotalHours"] = ""
            allData_dict["Error"] = "No entries were found."
            
            allData_arr.append(allData_dict)
            
        print(allData_arr)
        return jsonify(allData_arr)

class getRecord(Resource):
    def get(self, Vol_ID):
        DBfile = "C:\\HOME\\Rik\\Projects\\Python\\VolunteerHours\\vhDatabase.accdb"
        conn = pyodbc.connect('Driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ='+DBfile)
        cur = conn.cursor()
        SQL = "SELECT FORMAT(Vol_Date,'MM/dd/yyyy') as VDate, Vol_Hours, Location FROM "
        SQL = SQL + "Volunteer_Hours "
        SQL = SQL + "WHERE Vol_ID = " + Vol_ID

        cur.execute(SQL)
        allData = cur.fetchall()

        cur.close()
        conn.close()


        allData_dict = {}
        allData_dict["Date"] = allData[0][0]
        allData_dict["Hours"] = allData[0][1]
        allData_dict["Location"] = allData[0][2]
            
        print(allData_dict)
        
        return jsonify(allData_dict)

class updateRecord(Resource):
    def get(self, volMonth, volDay, volYear, strHours, strLocation, Vol_ID):
        DBfile = "C:\\HOME\\Rik\\Projects\\Python\\VolunteerHours\\vhDatabase.accdb"
        conn = pyodbc.connect('Driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ='+DBfile)
        cur = conn.cursor()
        
        volDate = volMonth + "/" + volDay + "/" + volYear
        print(volDate)
        
        
        SQL = "UPDATE Volunteer_Hours SET Vol_Date = '" + volDate + "', Vol_Hours = " + strHours + ", Location = '" + strLocation + "' "
        SQL = SQL + "WHERE Vol_ID = " + Vol_ID

        cur.execute(SQL)
        conn.commit()
        cur.close()
        conn.close()

        message = {}
        
        regMessage = "SUCCESS"

        message["Message"] = regMessage
        
        return jsonify(message)   

class deleteRecord(Resource):
    def get(self, Vol_ID):
        DBfile = "C:\\HOME\\Rik\\Projects\\Python\\VolunteerHours\\vhDatabase.accdb"
        conn = pyodbc.connect('Driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ='+DBfile)
        cur = conn.cursor()
        
        SQL = "DELETE FROM Volunteer_Hours WHERE Vol_ID = " + Vol_ID

        cur.execute(SQL)
        conn.commit()
        cur.close()
        conn.close()

        message = {}
        
        regMessage = "SUCCESS"

        message["Message"] = regMessage
        
        return jsonify(message)   
    
objapi.add_resource(getUser, '/getUser/<strUsername>/<strPassword>/<blEditProfile>')
objapi.add_resource(Register, "/register/<strFirstName>/<strLastName>/<strUsername>/<strPassword>/<strGrade>/<strStudentNum>/<strCatID>")
objapi.add_resource(updateProfile, "/updateProfile/<strFirstName>/<strLastName>/<strUsername>/<strPassword>/<strGrade>/<strStudentNum>/<strCatID>/<strUserID>")
objapi.add_resource(getAwards, "/getAwards")
objapi.add_resource(inputHours, '/inputHours/<userID>/<volMonth>/<volDay>/<volYear>/<volHours>/<strLocation>')
objapi.add_resource(viewHours, '/viewHours/<monthStart>/<dayStart>/<yearStart>/<monthEnd>/<dayEnd>/<yearEnd>/<userID>')
objapi.add_resource(getRecord, '/getRecord/<Vol_ID>')
objapi.add_resource(updateRecord, '/updateRecord/<volMonth>/<volDay>/<volYear>/<strHours>/<strLocation>/<Vol_ID>')
objapi.add_resource(deleteRecord, '/deleteRecord/<Vol_ID>')

#app.run(debug=True)
app.run(host='0.0.0.0', port=5000)

    





