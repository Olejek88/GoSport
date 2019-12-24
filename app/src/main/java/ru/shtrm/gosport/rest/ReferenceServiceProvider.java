package ru.shtrm.gosport.rest;

import android.content.Context;
import android.os.Bundle;

public class ReferenceServiceProvider implements IServiceProvider {

	private final Context mContext;

	/**
	 *
	 */
	public ReferenceServiceProvider(Context context) {
		mContext = context;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see ru.toir.mobile.rest.IServiceProvider#RunTask(int, android.os.Bundle)
	 */
	@Override
	public Bundle RunTask(int method, Bundle extras) {

		switch (method) {
		case Methods.GET_AMPLUA:
			return getAmplua(extras);
		case Methods.GET_EVENT:
			return getEvents(extras);
		case Methods.GET_LEVEL:
			return getLevels(extras);
		case Methods.GET_SPORT:
			return getSports(extras);
		case Methods.GET_STADIUM:
			return getStadiums(extras);
        case Methods.GET_TEAM:
			return getTeams(extras);
        /*
		case Methods.GET_TRAINING:
			return getTrainings(extras);
		case Methods.GET_PLAYERS:
			return getUsers(extras);
		case Methods.GET_USER_SPORT:
			return getUserSports(extras);
		case Methods.GET_USER_TRAINING:
			return getUserTrainings(extras);*/
		}

		Bundle result = new Bundle();
		result.putBoolean(IServiceProvider.RESULT, false);
		result.putString(MESSAGE, "Запуск не существующей задачи сервиса.");
		return result;

	}

	/**
	 *
	 */
	private Bundle getAmplua(Bundle extras) {
		try {
            return null;
        } catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(MESSAGE, e.getMessage());
			return result;
		}
	}

	/**
	 *
	 */
	private Bundle getEvents(Bundle extras) {
		try {
//			return new ReferenceProcessor(mContext).getOperationPattern(extras);
            return null;
        } catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(MESSAGE, e.getMessage());
			return result;
		}
	}

	/**
	 *
	 */
	private Bundle getLevels(Bundle extras) {
		try {
			return new ReferenceProcessor(mContext).getLevels(extras);
		} catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(MESSAGE, e.getMessage());
			return result;
		}
	}

	/**
	 *
	 */
	private Bundle getSports(Bundle extras) {
		try {
			return new ReferenceProcessor(mContext).getSports(extras);
		} catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(MESSAGE, e.getMessage());
			return result;
		}
	}

	/**
	 *
	 */
	private Bundle getStadiums(Bundle extras) {
		try {
			return new ReferenceProcessor(mContext).getStadiums(extras);
		} catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(MESSAGE, e.getMessage());
			return result;
		}
	}

	private Bundle getTeams(Bundle extras) {
		try {
			return new ReferenceProcessor(mContext).getTeams(extras);
		} catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(MESSAGE, e.getMessage());
			return result;
		}
	}

	/**
	 *
	 */
	/*
	private Bundle getTrainings(Bundle extras) {
		try {
			return new ReferenceProcessor(mContext).getTrainings(extras);
		} catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(MESSAGE, e.getMessage());
			return result;
		}
	}

	/*
	private Bundle getUsers(Bundle extras) {
		try {
			return new ReferenceProcessor(mContext).getOperationType(extras);
		} catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(MESSAGE, e.getMessage());
			return result;
		}
	}*/

	/*
	private Bundle getUserSports(Bundle extras) {
		try {
			return new ReferenceProcessor(mContext).getTaskStatus(extras);
		} catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(MESSAGE, e.getMessage());
			return result;
		}
	}*/

	/*
	private Bundle getUserTrainings(Bundle extras) {
		try {
//			return new ReferenceProcessor(mContext).getEquipment(extras);
            return null;
        } catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(MESSAGE, e.getMessage());
			return result;
		}
	}*/
	/**
	 *
	 */
	private Bundle getAll(Bundle extras) {
		try {
//			return new ReferenceProcessor(mContext).getAll(extras);
            return null;
        } catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(MESSAGE, e.getMessage());
			return result;
		}
	}


	public static class Methods {
		public static final int GET_AMPLUA = 1;
		public static final int GET_LEVEL = 2;
		public static final int GET_SPORT = 3;
		public static final int GET_PLAYERS = 4;
		public static final int GET_STADIUM = 5;
		public static final int GET_USER_TRAINING = 6;
		public static final int GET_USER_SPORT = 7;
		public static final int GET_EVENT = 8;
		public static final int GET_TEAM = 9;
		public static final int GET_TRAINING = 10;

        public static final int GET_ALL = 11;

        /*
		public static final String GET_AMLUA_UUID = "ampluaUuid";
		public static final String GET__UUID = "documentationUuid";
		public static final String GET_OPERATION_RESULT_PARAMETER_UUID = "operationResultUuid";
		public static final String GET_EQUIPMENT_PARAMETER_UUID = "equipmentUuid";
		public static final String GET_DOCUMENTATION_FILE_PARAMETER_UUID = "documentationFileUuid";
		public static final String GET_IMAGE_FILE_PARAMETER_UUID = "imageFileUuid";
		public static final String RESULT_GET_DOCUMENTATION_FILE_UUID = "loadedUuid";
        */
	}

	public static class Actions {
		public static final String ACTION_GET_ALL = "action_get_all";
		public static final String ACTION_GET_AMPLUA = "action_get_amplua";
		public static final String ACTION_GET_LEVELS = "action_get_levels";
		public static final String ACTION_GET_TEAMS = "action_get_teams";
		public static final String ACTION_GET_STADIUMS = "action_get_stadiums";
		public static final String ACTION_GET_TRAININGS = "action_get_trainings";
		public static final String ACTION_GET_USER_TRAININGS = "action_get_user_trainings";
		public static final String ACTION_GET_USER_SPORTS = "action_get_user_sports";
		public static final String ACTION_GET_PLAYERS = "action_get_players";
		public static final String ACTION_GET_SPORTS = "action_get_sports";
		public static final String ACTION_GET_EVENTS = "action_get_events";
	}

}
