use EmployeeMng

--CAU A:
CREATE PROCEDURE UpdateEmployeeSalary @_IDEm INT, 
									  @_Rate MONEY, 
									  @_ChangeDate DATETIME,
									  @_PayFreq TINYINT
AS
BEGIN
	DECLARE @check INT;
	SET @check = 0;
	SELECT @check = EPH.BusinessEntityID 
	FROM EmployeePayHistory AS EPH
	WHERE EPH.BusinessEntityID = @_IDEm AND
		  EPH.RateChangeDate = @_ChangeDate

	IF (@check = 0 AND (@_Rate BETWEEN 6.5 AND 200)) 
		BEGIN
			INSERT INTO EmployeePayHistory
			VALUES (@_IDEm, @_ChangeDate, @_Rate, @_PayFreq, GETDATE());
		END
END

--exec UpdateEmployeeSalary @_IDEm = 3, @_Rate = 123, @_ChangeDate = '2020-03-14', @_PayFreq = 1;

GO

--CAU B:
CREATE PROCEDURE FindEmployee 
(
	@_DepartmentID INT = 0,
	@_ShiftID INT = 0,
	@_Gender NVARCHAR(5) = ''
)
AS
BEGIN
	SET NOCOUNT ON;

	DECLARE @sql VARCHAR(MAX);
	DECLARE @paramenter VARCHAR(1000);

	SET @sql = 'SELECT E.BusinessEntityID, E.JobTitle, E.BirthDate, E.HireDate, E.CurrentFlag
				FROM EmployeeDepartmentHistory AS EDH
				INNER JOIN Employee AS E ON EDH.BusinessEntityID = E.BusinessEntityID
				WHERE EDH.EndDate IS NULL';
	IF(@_DepartmentID <> 0) 
		SET @sql = CONCAT(@sql, ' AND EDH.DepartmentID = ', @_DepartmentID);

	IF(@_ShiftID <> 0) 
		SET @sql = CONCAT(@sql, ' AND EDH.ShiftID = ', @_ShiftID);

	IF(LEN(@_Gender) > 2) 
		SET @sql = @sql + ' AND E.Gender = ' + @_Gender;

	EXECUTE(@sql)
END

--exec FindEmployee @_DepartmentID = 7, @_ShiftID = 0, @_Gender = "''";

--CAU C:
CREATE TRIGGER DeleteEmployee
ON EmployeeDepartmentHistory
FOR DELETE
AS
BEGIN
	DECLARE @_ID INT;
	SELECT DISTINCT @_ID = BusinessEntityID
	FROM deleted

	ROLLBACK TRAN

	UPDATE Employee
	SET CurrentFlag = 0
	WHERE BusinessEntityID = @_ID

	UPDATE EmployeeDepartmentHistory
	SET EndDate = GETDATE()
	WHERE EndDate IS NULL AND
		  BusinessEntityID = @_ID
END

--DELETE FROM EmployeeDepartmentHistory WHERE BusinessEntityID = 1
--UPDATE EmployeeDepartmentHistory SET EndDate = NULL WHERE BusinessEntityID = 1
--UPDATE Employee SET CurrentFlag = 1 WHERE BusinessEntityID = 1

GO 

--CAU D:
CREATE TRIGGER InsertEmployeeDepartmentHistory 
ON EmployeeDepartmentHistory
FOR INSERT
AS
IF (SELECT COUNT(*)
	FROM EmployeeDepartmentHistory AS EDH
	INNER JOIN inserted on inserted.BusinessEntityID = EDH.BusinessEntityID
	WHERE EDH.EndDate IS NULL) > 1
BEGIN
	PRINT 'Khong the phan cong nhan vien vao phong ban khac'
	ROLLBACK TRAN
END

GO

--CAU E:
CREATE VIEW TimeWork AS 
-- danh sach nv van dang con lam viec
SELECT EDH1.BusinessEntityID, MIN(EDH1.StartDate) 'StartDateWork', NULL 'EndDateWork'
FROM EmployeeDepartmentHistory AS EDH1 
GROUP BY EDH1.BusinessEntityID
HAVING EDH1.BusinessEntityID IN (SELECT E.BusinessEntityID 
								  FROM EmployeeDepartmentHistory AS E 
								  WHERE E.EndDate IS NULL)
UNION 
-- danh sach nhung nguoi tung lam viec
SELECT EDH2.BusinessEntityID, MIN(EDH2.StartDate) 'StartDateWork', MAX(EDH2.EndDate) 'EndDateWork'
FROM EmployeeDepartmentHistory AS EDH2
WHERE EDH2.EndDate IS NOT NULL
GROUP BY EDH2.BusinessEntityID
HAVING EDH2.BusinessEntityID NOT IN (SELECT E.BusinessEntityID 
									  FROM EmployeeDepartmentHistory AS E 
									  WHERE E.EndDate IS NULL)

GO

CREATE FUNCTION CalcDayWork( @StartDate DATE, @EndDate DATE)
RETURNS INT AS
BEGIN
RETURN
-- giải thích công thức

  (DATEDIFF(dd, @StartDate, @EndDate) + 1) --tính ngày số ngày kể cả 2 mốc, 
  -(DATEDIFF(wk, @StartDate, @EndDate) * 2) -- tính số ngày t7, cn 
  -- hàm dateiff tính theo wk sẽ xét: 2 ngày cách nhau 0 tuần khi nằm trong cùng một
  -- chuỗi ngày từ chủ nhật -> thứ 7 (không phải từ t2 - > chủ nhật)
  -- do đó nếu số tuần cách nhau 1 tức là có ít nhất 1 ngày t7, 1 ngày cn --> 2 ngày
  -- nên nhân 2 cho mỗi tuần

  -- trừ mốc chủ nhật vì chưa tính được trong công thức trên
  -(CASE WHEN DATENAME(dw, @StartDate) = 'Sunday' THEN 1 ELSE 0 END) 

  -- trừ mốc thứ 7 vì chưa tính được trong công thức trên
  -(CASE WHEN DATENAME(dw, @EndDate) = 'Saturday' THEN 1 ELSE 0 END)
END

GO

-- Thuật toán tính lương theo từng năm của từng nhân viên
-- Lấy năm nhỏ nhất
-- Thêm các nhân viên vào bảng nv hiện tại theo từng năm kể từ năm nhỏ nhất
-- Tính tổng lương theo từng năm lưu vào bảng
-- tăng biến năm
-- Cập nhật bảng nv hiện tại

CREATE FUNCTION SalaryPerYear ()
RETURNS @TableSalary TABLE (_Year INT, _IDEm INT, _Salary MONEY)
BEGIN
	DECLARE @_CurentYear INT = YEAR(GETDATE()),--gia tri nam hien tai
			@_Year INT = 0;--năm đang xét
			DECLARE @CurrentEmployeePerYear TABLE (_IDEm INT, _Rate MONEY)
	-- lay gia tri nam bat dau
	SELECT @_Year =  MIN(YEAR(EPH.RateChangeDate)) 
	FROM EmployeePayHistory AS EPH
	
	WHILE @_Year <= @_CurentYear
	BEGIN
	-- thêm nhan viên mới kèm rate khởi điểm
	-- các nv cũ được giữ nguyên kèm rate hiện tại theo năm đang xét của họ
		INSERT INTO	@CurrentEmployeePerYear 
		SELECT		EPH.BusinessEntityID, EPH.Rate
		FROM		EmployeePayHistory AS EPH 
		WHERE		YEAR(EPH.RateChangeDate) = @_Year
		GROUP BY	EPH.BusinessEntityID, EPH.Rate,RateChangeDate
		HAVING		EPH.BusinessEntityID  NOT IN (SELECT CEM._IDEm
												  FROM @CurrentEmployeePerYear AS CEM)
				AND EPH.RateChangeDate <= ALL (SELECT E.RateChangeDate
											   FROM EmployeePayHistory AS E 
											   WHERE E.BusinessEntityID = EPH.BusinessEntityID)

		INSERT INTO @TableSalary 
		SELECT	@_Year, CEPY._IDEm , [dbo].[GetSalaryPerYear](CEPY._IDEm, CEPY._Rate, @_Year) 
		FROM @CurrentEmployeePerYear AS CEPY

		-- xoa nhan vien da nghi
		DELETE  @CurrentEmployeePerYear 
		WHERE _IDEm = (SELECT	TW.BusinessEntityID
						FROM	TimeWork as  TW
						WHERE	TW.BusinessEntityID = _IDEm
							AND TW.[EndDateWork] IS NOT NULL
							AND YEAR(TW.EndDateWork) = @_Year)

		SET @_Year = @_Year + 1;
	END
    RETURN
END

GO

--LAY THOI GIAN CA LAM CUA 1 NHAN VIEN TAI 1 MOC THOI GIAN
CREATE FUNCTION GetShift(@_IDEm INT, @_Time DATE)
RETURNS INT AS
BEGIN
	DECLARE @_Hour INT;
	SELECT @_Hour = (CASE
						WHEN S.EndTime > S.StartTime THEN DATEDIFF(hour, S.StartTime, S.EndTime)
						ELSE DATEDIFF(hour, S.StartTime, S.EndTime) + 24
					 END)
	FROM EmployeeDepartmentHistory as EDH
	INNER JOIN Shift AS S ON S.ShiftID = EDH.ShiftID
	WHERE EDH.BusinessEntityID = @_IDEm AND
		  (EDH.StartDate <= @_Time AND 
		   (EDH.EndDate >= @_Time OR EDH.EndDate IS NULL))
	RETURN @_Hour	   
END

GO

-- Tính lương theo 1 năm cụ thể của 1 nhân viên cụ thể
CREATE FUNCTION GetSalaryPerYear(@_IDEm INT, @_OldRate MONEY, @_Year INT)
RETURNS MONEY AS
BEGIN
	DECLARE @TableRate TABLE (_RateChangeDate DATETIME, _Rate MONEY);
	DECLARE @_SalaryTemp MONEY = 0,--tổng lương trong một năm cụ thể
			@_Check INT, -- biến kiểm tra
			@_Rate MONEY = 0; -- lương tính theo giờ của nhân viên, được lấy từ dữ liệu

	DECLARE @_RateDateChangeTemp DATE,-- biến tạm lưu ngày thay đổi rate
			@_Start DATE = NULL,
			@_End DATE = NULL;--ngày bắt đầu và kết thúc việc tính lương

	-------------------- insert du lieu của nhân viên id trong năm @year  --------------
	INSERT INTO @TableRate 
	SELECT E.RateChangeDate, E.Rate 
	FROM EmployeePayHistory AS E 
	WHERE E.BusinessEntityID = @_IDEm AND
		  YEAR(E.RateChangeDate) = @_Year;

	--kiem tra nhan vien có còn làm việc không
	-- lấy ngày làm việc cuối cùng nằm trong năm @year
	SELECT  @_End = TW.[EndDateWork] 
	FROM TimeWork  AS TW
	WHERE TW.BusinessEntityID = @_IDEm AND
		  TW.EndDateWork IS NOT NULL AND
		  YEAR(TW.EndDateWork) = @_Year;

	IF @_End IS NOT NULL --nhan vien nghỉ việc trong năm nay
		BEGIN 
			SET @_RateDateChangeTemp = @_End;
		END
	ELSE -- nhân viên vẫn còn làm việc
		BEGIN
			SET @_End  = CAST('12/31/'+  CONVERT(VARCHAR(10),@_Year) AS DATE);
		END

	--kiem tra nhan vien cu hay nhan vien moi
	IF EXISTS  (SELECT TW.BusinessEntityID 
				FROM TimeWork AS TW-- nhan vien cu
				where	TW.BusinessEntityID = @_IDEm AND
						YEAR(TW.[StartDateWork]) < @_Year)
		BEGIN
			SET @_Start = CAST('01/01/' + CONVERT(VARCHAR(10),@_Year) AS DATE);
			IF EXISTS (SELECT * 
					   FROM @TableRate)--nhan vien co duoc tang luong trong nam nay
				BEGIN
					SELECT  @_RateDateChangeTemp  =  CAST(MIN(TR._RateChangeDate) AS DATE)
					FROM @TableRate TR;
					SET @_SalaryTemp = @_SalaryTemp + 
									   dbo.CalcDayWork(@_Start,DATEADD(DAY, -1 ,@_RateDateChangeTemp))
									   * dbo.GetShift(@_IDEm, @_Start) * @_OldRate;
				END
			ELSE -- nhân viên không còn dc tăng lương, trả về kết quả -> kết thúc
				BEGIN
					RETURN  dbo.CalcDayWork (@_Start, @_End) * dbo.GetShift(@_IDEm, @_Start) * @_OldRate;
				END
		END
	ELSE -- nhân viên mới
	-- Phần này nhân viên cũ và mới tính giống nhau
		SELECT @_Check =  COUNT(*) 
		FROM @TableRate;

	WHILE (@_Check > 1)
		 BEGIN
			SET @_Check = @_Check - 1;
			--gan moc thoi gian bat dau
			SELECT  @_Start  =  MIN(TR._RateChangeDate)  
			FROM @TableRate AS TR ;

			--set lại rate mới
			SELECT @_Rate = TR._Rate 
			FROM @TableRate TR 
			WHERE TR._RateChangeDate = @_Start

			-- xoa moc da lấy ra
			DELETE FROM @TableRate WHERE _RateChangeDate = @_Start;

			-- lấy mốc thứ hai 
			SELECT  @_RateDateChangeTemp  =  MIN(TR._RateChangeDate)  
			FROM @TableRate AS TR ;

			--tính lương trong khoảng giữa 2 mốc không tính ngày @rate_date_change_temp
			SET @_SalaryTemp = @_salaryTemp + 
							   dbo.CalcDayWork(@_Start, DATEADD(DAY, -1, @_RateDateChangeTemp)) * dbo.GetShift(@_IDEm, @_Start) * @_Rate
		 END
	 --tinh moc cuoi cung cho den het nam
	 --set lại rate mới
	SELECT @_Rate = TR._Rate 
	FROM @TableRate AS TR ;

	SELECT @_Start = TR._RateChangeDate 
	FROM @TableRate AS TR; 
	SET @_SalaryTemp = @_SalaryTemp + ISNULL(dbo.CalcDayWork (@_Start, @_End),0) * dbo.GetShift(@_IDEm, @_Start) * @_Rate;

	--trả về kq
	RETURN @_SalaryTemp;
END

GO

CREATE FUNCTION GetEmployeeSalaryPerYear()
RETURNS TABLE RETURN
	SELECT SPY._IDEm, SPY._Year, SPY._Salary
	FROM SalaryPerYear() AS SPY
	GROUP BY SPY._IDEm, SPY._Salary, SPY._Year;

GO

--CAU F:
CREATE VIEW [SalaryRate] AS
SELECT EPH1.BusinessEntityID, EPH1.Rate
FROM EmployeePayHistory AS EPH1
WHERE EPH1.RateChangeDate <= GETDATE() AND
	  EPH1.RateChangeDate >= ALL (SELECT EPH2.RateChangeDate
								  FROM EmployeePayHistory AS EPH2
								  WHERE EPH2.BusinessEntityID = EPH1.BusinessEntityID AND
										EPH2.RateChangeDate <= GETDATE())

GO

--VIEW LAY THOI GIAN CA LAM HIEN TAI CUA TUNG NHAN VIEN
CREATE VIEW [EmployeeWorkHour] AS
SELECT EDH.BusinessEntityID, 
(CASE
	WHEN S.EndTime > S.StartTime THEN DATEDIFF(hour, S.StartTime, S.EndTime)
	ELSE DATEDIFF(hour, S.StartTime, S.EndTime) + 24
 END) AS WorkHours 
FROM EmployeeDepartmentHistory AS EDH
INNER JOIN Shift AS S ON S.ShiftID = EDH.ShiftID
WHERE EDH.EndDate IS NULL

GO

CREATE FUNCTION GetDepartmentSalary()
RETURNS TABLE RETURN
	SELECT D.DepartmentID, D.Name, D.GroupName, SUM(SR.Rate*EWH.WorkHours*20) AS Salary
	FROM ((SalaryRate AS SR
	INNER JOIN EmployeeWorkHour AS EWH ON EWH.BusinessEntityID = SR.BusinessEntityID)
	INNER JOIN EmployeeDepartmentHistory AS EDH ON EDH.BusinessEntityID = SR.BusinessEntityID)
	INNER JOIN Department AS D ON D.DepartmentID = EDH.DepartmentID
	WHERE EDH.EndDate IS NULL
	GROUP BY D.DepartmentID, D.Name, D.GroupName

