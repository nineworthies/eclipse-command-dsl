director {
	destination "eclipse-install-path"
	install {
		unitsFromRepository ("http://an.update/site") {
			installableUnit {
				id "a.feature.group"
			}
		}
	}
}