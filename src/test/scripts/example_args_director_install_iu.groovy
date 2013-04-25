include "include/example_args_director_included.groovy"
director {
	install {
		unitsFromRepository("http://an.update/site") {
			installableUnit {
				id "a.feature.group"
			}
		}
	}
}