.PHONY: all qpa-s qpa-l gb-s gb-l hack-s hack-l

all:
	echo "qpa-s|qpa-l|gb-s|gb-l|hack-s|hack-l"

qpa-s:
	git checkout staging
	git checkout deploy/qpa-staging
	git reset --hard origin/staging
	git push -f origin deploy/qpa-staging
	git checkout staging

qpa-l:
	git checkout staging
	git checkout deploy/qpa-live
	git reset --hard origin/staging
	git push -f origin deploy/qpa-live
	git checkout staging

gb-s:
	git checkout staging
	git checkout deploy/golyabal-staging
	git reset --hard origin/staging
	git push -f origin deploy/golyabal-staging
	git checkout staging

gb-l:
	git checkout staging
	git checkout deploy/golyabal-live
	git reset --hard origin/staging
	git push -f origin deploy/golyabal-live
	git checkout staging

hack-s:
	git checkout staging
	git checkout deploy/hackathon-staging
	git reset --hard origin/staging
	git push -f origin deploy/hackathon-staging
	git checkout staging

hack-l:
	git checkout staging
	git checkout deploy/hackathon-live
	git reset --hard origin/staging
	git push -f origin deploy/hackathon-live
	git checkout staging

gtb-s:
	git checkout staging
	git checkout deploy/gtb-staging
	git reset --hard origin/staging
	git push -f origin deploy/gtb-staging
	git checkout staging

gtb-l:
	git checkout staging
	git checkout deploy/gtb-live
	git reset --hard origin/staging
	git push -f origin deploy/gtb-live
	git checkout staging
