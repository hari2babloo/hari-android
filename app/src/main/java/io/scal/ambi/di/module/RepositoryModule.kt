package io.scal.ambi.di.module

import dagger.Binds
import dagger.Module
import io.scal.ambi.model.repository.auth.AuthRepository
import io.scal.ambi.model.repository.auth.IAuthRepository
import io.scal.ambi.model.repository.data.calendar.CalendarRepository
import io.scal.ambi.model.repository.data.calendar.ICalendarRepository
import io.scal.ambi.model.repository.data.chat.IChatRepository
import io.scal.ambi.model.repository.data.chat.TwilioChatRepository
import io.scal.ambi.model.repository.data.newsfeed.IPostsRepository
import io.scal.ambi.model.repository.data.newsfeed.PostsRepository
import io.scal.ambi.model.repository.data.user.IUserRepository
import io.scal.ambi.model.repository.data.user.UserRepository
import io.scal.ambi.model.repository.local.ILocalUserDataRepository
import io.scal.ambi.model.repository.local.LocalUserDataRepository
import javax.inject.Singleton

@Singleton
@Module
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindAuthRepository(authRepository: AuthRepository): IAuthRepository

    @Singleton
    @Binds
    abstract fun bindStudentRepository(studentRepository: UserRepository): IUserRepository

    @Singleton
    @Binds
    abstract fun bindNewsFeedRepository(newsFeedRepository: PostsRepository): IPostsRepository

    @Singleton
    @Binds
    abstract fun bindChatRepository(chatRepository: TwilioChatRepository): IChatRepository

    @Singleton
    @Binds
    abstract fun bindCalendarRepository(calendarRepository: CalendarRepository): ICalendarRepository

    @Singleton
    @Binds
    abstract fun bindLocalRepository(localUserDataRepository: LocalUserDataRepository): ILocalUserDataRepository
}