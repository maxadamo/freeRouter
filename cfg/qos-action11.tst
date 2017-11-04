description qos ingress shaper action

addrouter r1
int eth1 eth 0000.0000.1111 $1a$ $1b$
!
vrf def v1
 rd 1:1
 exit
policy-map p1
 seq 10 act sha
  access-rate 65536
 exit
int eth1
 vrf for v1
 ipv4 addr 1.1.1.1 255.255.255.0
 ipv6 addr 1234::1 ffff::
 service-policy-in p1
 exit
!

addrouter r2
int eth1 eth 0000.0000.2222 $1b$ $1a$
!
vrf def v1
 rd 1:1
 exit
policy-map p1
 seq 10 act pol
  access-rate 81920
 exit
int eth1
 vrf for v1
 ipv4 addr 1.1.1.2 255.255.255.0
 ipv6 addr 1234::2 ffff::
 service-policy-in p1
 exit
!


r2 tping 100 3 1.1.1.1 /vrf v1
r2 tping 100 3 1234::1 /vrf v1

r2 tping 100 5 1.1.1.1 /vrf v1 /rep 100 /tim 500 /siz 100
r2 tping 100 5 1234::1 /vrf v1 /rep 100 /tim 500 /siz 100
r1 tping 100 5 1.1.1.2 /vrf v1 /rep 100 /tim 500 /siz 100
r1 tping 100 5 1234::2 /vrf v1 /rep 100 /tim 500 /siz 100

r2 output show policy eth1 in
output ../binTmp/qos-shape.html
<html><body bgcolor="#000000" text="#FFFFFF" link="#00FFFF" vlink="#00FFFF" alink="#00FFFF">
here is the policy:
<pre>
<!>show:0
</pre>
</body></html>
!
