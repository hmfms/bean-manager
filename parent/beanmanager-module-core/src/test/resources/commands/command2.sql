select ot.NumAgent, minute, NVL(a.c,0) as countrel  from OT_Allotment ot left outer join
	(select NVL(count(*),-1) as c ,idallotment as id from OT_Reservation_Allotment group by idallotment) a
	on a.id = ot.idallotment where activities in %s
	and ot.NumAgent >= NVL(a.c,0) and dateallotment = ?