package com.example.nobelapi.data.repository

import com.example.nobelapi.domain.model.Laureate
import com.example.nobelapi.domain.model.NobelPrize
import com.example.nobelapi.domain.repository.PrizeRepository

class InMemoryPrizeRepository : PrizeRepository {
    private val prizes = listOf(
        NobelPrize(
            year = 2023,
            category = "physics",
            categoryFullName = "The Nobel Prize in Physics",
            dateAwarded = "2023-10-03",
            description = "Awarded for experimental methods that generate attosecond pulses of light.",
            laureates = listOf(
                Laureate(
                    id = 1026,
                    fullName = "Pierre Agostini",
                    birthCountry = "France",
                    motivation = "for experimental methods that generate attosecond pulses of light for the study of electron dynamics in matter"
                ),
                Laureate(
                    id = 1027,
                    fullName = "Ferenc Krausz",
                    birthCountry = "Hungary",
                    motivation = "for experimental methods that generate attosecond pulses of light for the study of electron dynamics in matter"
                ),
                Laureate(
                    id = 1028,
                    fullName = "Anne L'Huillier",
                    birthCountry = "France",
                    motivation = "for experimental methods that generate attosecond pulses of light for the study of electron dynamics in matter"
                )
            )
        ),
        NobelPrize(
            year = 2023,
            category = "chemistry",
            categoryFullName = "The Nobel Prize in Chemistry",
            dateAwarded = "2023-10-04",
            description = "Awarded for the discovery and synthesis of quantum dots.",
            laureates = listOf(
                Laureate(
                    id = 1029,
                    fullName = "Moungi G. Bawendi",
                    birthCountry = "France",
                    motivation = "for the discovery and synthesis of quantum dots"
                ),
                Laureate(
                    id = 1030,
                    fullName = "Louis E. Brus",
                    birthCountry = "United States",
                    motivation = "for the discovery and synthesis of quantum dots"
                ),
                Laureate(
                    id = 1031,
                    fullName = "Aleksey Yekimov",
                    birthCountry = "Russia",
                    motivation = "for the discovery and synthesis of quantum dots"
                )
            )
        ),
        NobelPrize(
            year = 2022,
            category = "peace",
            categoryFullName = "The Nobel Peace Prize",
            dateAwarded = "2022-10-07",
            description = "Awarded to human rights defenders and organizations from Eastern Europe.",
            laureates = listOf(
                Laureate(
                    id = 1017,
                    fullName = "Ales Bialiatski",
                    birthCountry = "Belarus",
                    motivation = "for promoting the right to criticize power and protect the fundamental rights of citizens"
                ),
                Laureate(
                    id = 1018,
                    fullName = "Memorial",
                    birthCountry = "Russia",
                    motivation = "for documenting war crimes, human rights abuses and the abuse of power"
                ),
                Laureate(
                    id = 1019,
                    fullName = "Center for Civil Liberties",
                    birthCountry = "Ukraine",
                    motivation = "for efforts to document war crimes and protect human rights"
                )
            )
        ),
        NobelPrize(
            year = 1901,
            category = "literature",
            categoryFullName = "The Nobel Prize in Literature",
            dateAwarded = "1901-11-14",
            description = "The first Nobel Prize in Literature.",
            laureates = listOf(
                Laureate(
                    id = 569,
                    fullName = "Sully Prudhomme",
                    birthCountry = "France",
                    motivation = "in special recognition of his poetic composition"
                )
            )
        )
    )

    override fun getAll(): List<NobelPrize> = prizes

    override fun find(year: Int, category: String): NobelPrize? {
        return prizes.firstOrNull { prize ->
            prize.year == year && prize.category.equals(category, ignoreCase = true)
        }
    }

    override fun getLaureates(year: Int, category: String): List<Laureate>? {
        return find(year = year, category = category)?.laureates
    }
}
