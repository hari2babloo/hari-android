package io.scal.ambi.entity.organization

import io.scal.ambi.extensions.view.IconImage

sealed class Organization(open val id: String,
                          open val name: String,
                          open val slug: String,
                          open val description: String,
                          open val bannerPicture: IconImage,
                          open val chatUids: List<String>,
                          val type: OrganizationType) {

    data class Class(override val id: String,
                     override val name: String,
                     override val slug: String,
                     override val description: String,
                     override val bannerPicture: IconImage,
                     override val chatUids: List<String>) : Organization(id, name, slug, description, bannerPicture, chatUids, OrganizationType.CLASS)

    data class Group(override val id: String,
                     override val name: String,
                     override val slug: String,
                     override val description: String,
                     override val bannerPicture: IconImage,
                     override val chatUids: List<String>) : Organization(id, name, slug, description, bannerPicture, chatUids, OrganizationType.GROUP)

    data class Community(override val id: String,
                         override val name: String,
                         override val slug: String,
                         override val description: String,
                         override val bannerPicture: IconImage,
                         override val chatUids: List<String>) : Organization(id,
                                                                             name,
                                                                             slug,
                                                                             description,
                                                                             bannerPicture,
                                                                             chatUids,
                                                                             OrganizationType.COMMUNITY)
}