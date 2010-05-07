select count(*) as availableslot, dateallotment from
	(select ot.idallotment, ot.NumAgent, NVL(a.c,0) as countrel , minutes, dateallotment, ot.activities  from OT_Allotment ot  left outer  join
	(select NVL(count(*),-1) as c ,idallotment as id from OT_Reservation_Allotment group by idallotment) a
	on a.id = ot.idallotment where
	dateallotment >= ? and dateallotment <= ?
	and ot.NumAgent > 0 and
	activities in %s
	and ot.NumAgent >= NVL(a.c,0) ) p group by dateallotment