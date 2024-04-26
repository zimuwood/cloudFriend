declare namespace API {
    type UserType = {
        id: number;
        username?: string;
        userAccount: string;
        avatarUrl?: string;
        gender?: number;
        phone?: string;
        email?: string;
        userStatus?: number;
        userRole?: number;
        createTime?: Date;
        tags?: string[];
        profile?: string;
    };

    type TeamUserVo = {
        id: number;
        teamAvatar: string;
        teamName: string;
        teamDescription?: string;
        maxNum: number;
        currentNum: number;
        expireTime: Date;
        createTime: Date;
        captainId: number;
        teamStatus: number;
        userList: UserType[];
    }

    type Response<T> = {
        code: number;
        data: T;
        message: string;
        description?: string;
    }
}