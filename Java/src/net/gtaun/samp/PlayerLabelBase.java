/**
 * Copyright (C) 2011 MK124
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.gtaun.samp;

import net.gtaun.samp.data.Point;
import net.gtaun.samp.data.PointRange;

/**
 * @author MK124
 *
 */

public class PlayerLabelBase extends LabelBase
{
	PlayerBase player;
	
	
	public PlayerLabelBase( PlayerBase player, String text, int color, Point point, float drawDistance, boolean testLOS )
	{
		this.player = player;
		this.text = text;
		this.color = color;
		this.position = new PointRange( point, drawDistance );
		this.testLOS = testLOS;
		
		init();
	}

	public PlayerLabelBase( PlayerBase player, String text, int color, Point point, float drawDistance, boolean testLOS, PlayerBase attachedPlayer )
	{
		this.player = player;
		this.text = text;
		this.color = color;
		this.position = new PointRange( point, drawDistance );
		this.testLOS = testLOS;
		this.attachedPlayer = attachedPlayer;
		
		init();
	}
	
	public PlayerLabelBase( PlayerBase player, String text, int color, Point point, float drawDistance, boolean testLOS, VehicleBase attachedVehicle )
	{
		this.player = player;
		this.text = text;
		this.color = color;
		this.position = new PointRange( point, drawDistance );
		this.testLOS = testLOS;
		this.attachedVehicle = attachedVehicle;
		
		init();
	}

	public PlayerLabelBase( PlayerBase player, String text, int color, PointRange point, boolean testLOS )
	{
		this.player = player;
		this.text = text;
		this.color = color;
		this.position = point.clone();
		this.testLOS = testLOS;
		
		init();
	}
	
	public PlayerLabelBase( PlayerBase player, String text, int color, PointRange point, boolean testLOS, PlayerBase attachedPlayer )
	{
		this.player = player;
		this.text = text;
		this.color = color;
		this.position = point.clone();
		this.testLOS = testLOS;
		this.attachedPlayer = attachedPlayer;
		
		init();
	}
	
	public PlayerLabelBase( PlayerBase player, String text, int color, PointRange point, boolean testLOS, VehicleBase attachedVehicle )
	{
		this.player = player;
		this.text = text;
		this.color = color;
		this.position = point.clone();
		this.testLOS = testLOS;
		this.attachedVehicle = attachedVehicle;
		
		init();
	}
	
	private void init()
	{
		int playerId = GameModeBase.INVALID_PLAYER_ID, vehicleId = GameModeBase.INVALID_VEHICLE_ID;
		
		if( attachedPlayer != null )	playerId = attachedPlayer.id;
		if( attachedVehicle != null )	vehicleId = attachedVehicle.id;
		
		if( attachedPlayer != null || attachedVehicle != null )
		{
			offsetX = position.x;
			offsetY = position.y;
			offsetZ = position.z;
		}
		
		id = NativeFunction.createPlayer3DTextLabel( player.id, text, color,
				position.x, position.y, position.z, position.distance, playerId, vehicleId, testLOS );
	}
	
//---------------------------------------------------------
	
	public void destroy()
	{
		NativeFunction.deletePlayer3DTextLabel( player.id, id );
		GameModeBase.instance.playerLabelPool[ id ] = null;
	}

	public void attach( PlayerBase player, float x, float y, float z )
	{
		NativeFunction.deletePlayer3DTextLabel( this.player.id, id );
		id = NativeFunction.createPlayer3DTextLabel( this.player.id, text, color, x, y, z, position.distance,
				player.id, GameModeBase.INVALID_VEHICLE_ID, testLOS );
	}

	public void attach( VehicleBase vehicle, float x, float y, float z )
	{
		NativeFunction.deletePlayer3DTextLabel( this.player.id, id );
		id = NativeFunction.createPlayer3DTextLabel( this.player.id, text, color, x, y, z, position.distance,
				GameModeBase.INVALID_PLAYER_ID, vehicle.id, testLOS );
	}
	
	public void update( int color, String text )
	{
		this.color = color;
		this.text = text;
		
		NativeFunction.updatePlayer3DTextLabelText( player.id, id, color, text );
	}
}
