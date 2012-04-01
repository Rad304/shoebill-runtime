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

package net.gtaun.shoebill.object;

import java.util.Collection;

import net.gtaun.shoebill.SampObjectPool;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.data.Vector3D;
import net.gtaun.shoebill.exception.CreationFailedException;
import net.gtaun.shoebill.samp.SampNativeFunction;

/**
 * @author MK124
 *
 */

public class PlayerObject implements IPlayerObject
{
	public static Collection<IPlayerObject> get( IPlayer player )
	{
		return Shoebill.getInstance().getManagedObjectPool().getPlayerObjects( player );
	}
	
	public static <T extends IPlayerObject> Collection<T> get( IPlayer player, Class<T> cls )
	{
		return Shoebill.getInstance().getManagedObjectPool().getPlayerObjects( player, cls );
	}
	

	void processPlayerObjectMoved()
	{
		speed = 0.0F;
	}
	
	private int id = INVALID_ID;
	private IPlayer player;
	
	private int modelId;
	private Location location;
	private float speed = 0.0F;
	private float drawDistance = 0;
	
	private IPlayer attachedPlayer;
	
	
	public PlayerObject( IPlayer player, int modelId, float x, float y, float z, float rx, float ry, float rz ) throws CreationFailedException
	{
		initialize( player, modelId, new Location(x, y, z), new Vector3D(rx, ry, rz), drawDistance );
	}
	
	public PlayerObject( IPlayer player, int modelId, float x, float y, float z, float rx, float ry, float rz, float drawDistance ) throws CreationFailedException
	{
		initialize( player, modelId, new Location(x, y, z), new Vector3D(rx, ry, rz), drawDistance );
	}
	
	public PlayerObject( IPlayer player, int modelId, Location loc, float rx, float ry, float rz ) throws CreationFailedException
	{
		initialize( player, modelId, new Location(loc), new Vector3D(rx, ry, rz), drawDistance );
	}
	
	public PlayerObject( IPlayer player, int modelId, Location loc, float rx, float ry, float rz, float drawDistance ) throws CreationFailedException
	{
		initialize( player, modelId, new Location(loc), new Vector3D(rx, ry, rz), drawDistance );
	}
	
	public PlayerObject( IPlayer player, int modelId, Location loc, Vector3D rot ) throws CreationFailedException
	{
		initialize( player, modelId, new Location(loc), new Vector3D(rot), drawDistance );
	}
	
	public PlayerObject( IPlayer player, int modelId, Location loc, Vector3D rot, float drawDistance ) throws CreationFailedException
	{
		initialize( player, modelId, new Location(loc), new Vector3D(rot), drawDistance );
	}
	
	private void initialize( IPlayer player, int modelId, Location loc, Vector3D rot, float drawDistance ) throws CreationFailedException
	{
		if( player.isOnline() == false ) throw new CreationFailedException();
		
		this.player = player;
		this.modelId = modelId;
		this.location = loc;
		this.drawDistance = drawDistance;
		
		id = SampNativeFunction.createPlayerObject( player.getId(), modelId, loc.getX(), loc.getY(), loc.getZ(), rot.getX(), rot.getY(), rot.getZ(), drawDistance );
		if( id == INVALID_ID ) throw new CreationFailedException();
		
		SampObjectPool pool = (SampObjectPool) Shoebill.getInstance().getManagedObjectPool();
		pool.setPlayerObject( player, id, this );
	}
	

	@Override
	public void destroy()
	{
		if( isDestroyed() ) return;
		
		if( player.isOnline() )
		{
			SampNativeFunction.destroyPlayerObject( player.getId(), id );

			SampObjectPool pool = (SampObjectPool) Shoebill.getInstance().getManagedObjectPool();
			pool.setPlayerObject( player, id, null );
		}

		id = INVALID_ID;
	}

	@Override
	public boolean isDestroyed()
	{
		return id == INVALID_ID;
	}

	@Override
	public IPlayer getPlayer()
	{
		return player;
	}

	@Override
	public int getId()
	{
		return id;
	}
	
	@Override
	public int getModelId()
	{
		return modelId;
	}
	
	@Override
	public float getSpeed()
	{
		if( isDestroyed() ) return 0.0F;
		if( player.isOnline() == false ) return 0.0F;
		
		if( attachedPlayer != null && attachedPlayer.isOnline() ) return attachedPlayer.getVelocity().speed3d();
		
		return speed;
	}
	
	@Override
	public float getDrawDistance()
	{
		return drawDistance;
	}
	
	@Override
	public IPlayer getAttachedPlayer()
	{
		return attachedPlayer;
	}
	
	@Override
	public IObject getAttachedObject()
	{
		return null;
	}
	
	@Override
	public IVehicle getAttachedVehicle()
	{
		return null;
	}
	
	@Override
	public Location getLocation()
	{
		if( isDestroyed() ) return null;
		if( player.isOnline() == false ) return null;
		
		SampNativeFunction.getPlayerObjectPos( player.getId(), id, location );
		return location.clone();
	}

	@Override
	public void setLocation( Vector3D pos )
	{
		if( isDestroyed() ) return;
		if( player.isOnline() == false ) return;
		
		location.set( pos );
		SampNativeFunction.setPlayerObjectPos( player.getId(), id, pos.getX(), pos.getY(), pos.getZ() );
	}
	
	@Override
	public void setLocation( Location loc )
	{
		if( isDestroyed() ) return;
		if( player.isOnline() == false ) return;
		
		location.set( loc );
		SampNativeFunction.setPlayerObjectPos( player.getId(), id, loc.getX(), loc.getY(), loc.getZ() );
	}
	
	@Override
	public Vector3D getRotate()
	{
		if( isDestroyed() ) return null;
		if( player.isOnline() == false ) return null;
		
		Vector3D rotate = new Vector3D();
		SampNativeFunction.getPlayerObjectRot( player.getId(), id, rotate );
		return rotate;
	}

	@Override
	public void setRotate( float rx, float ry, float rz )
	{
		if( isDestroyed() ) return;
		if( player.isOnline() == false ) return;
		
		SampNativeFunction.setPlayerObjectRot( player.getId(), id, rx, ry, rz );
	}
	
	@Override
	public void setRotate( Vector3D rot )
	{
		setRotate( rot.getX(), rot.getY(), rot.getZ() );
	}
	
	@Override
	public boolean isMoving()
	{
		if( isDestroyed() ) return false;
		if( player.isOnline() == false ) return false;
		
		return SampNativeFunction.isPlayerObjectMoving(player.getId(), id );
	}
	
	@Override
	public int move( float x, float y, float z, float speed )
	{
		if( isDestroyed() ) return 0;
		if( player.isOnline() == false ) return 0;
		
		if( attachedPlayer == null ) this.speed = speed;	
		return SampNativeFunction.movePlayerObject( player.getId(), id, x, y, z, speed, -1000.0f, -1000.0f, -1000.0f );
	}
	
	@Override
	public int move( float x, float y, float z, float speed, float rotX, float rotY, float rotZ )
	{
		if( isDestroyed() ) return 0;
		if( player.isOnline() == false ) return 0;
		
		if( attachedPlayer == null ) this.speed = speed;	
		return SampNativeFunction.movePlayerObject( player.getId(), id, x, y, z, speed, rotX, rotY, rotZ );
	}
	
	@Override
	public int move( Vector3D pos, float speed )
	{
		return move( pos.getX(), pos.getY(), pos.getZ(), speed );
	}
	
	@Override
	public int move( Vector3D pos, float speed, Vector3D rot )
	{
		return move( pos.getX(), pos.getY(), pos.getZ(), speed, rot.getX(), rot.getY(), rot.getZ() );
	}
	
	@Override
	public void stop()
	{
		if( isDestroyed() ) return;
		if( player.isOnline() == false ) return;
		
		speed = 0.0F;
		SampNativeFunction.stopPlayerObject( player.getId(), id );
	}
	
	@Override
	public void attach( IPlayer target, float x, float y, float z, float rx, float ry, float rz )
	{
		if( isDestroyed() ) return;
		if( player.isOnline() == false ) return;
		if( target.isOnline() == false ) return;
		
		SampNativeFunction.attachPlayerObjectToPlayer( player.getId(), id, target.getId(), x, y, z, rx, ry, rz );
		
		attachedPlayer = player;
		speed = 0.0F;
	}
	
	@Override
	public void attach( IPlayer target, Vector3D pos, Vector3D rot )
	{
		attach( target, pos.getX(), pos.getY(), pos.getZ(), rot.getX(), rot.getY(), rot.getZ() );
	}

	@Override
	public void attach( IObject object, float x, float y, float z, float rx, float ry, float rz, boolean syncRotation )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void attach( IObject object, Vector3D pos, Vector3D rot, boolean syncRotation )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void attach( IVehicle vehicle, float x, float y, float z, float rx, float ry, float rz )
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void attach( IVehicle vehicle, Vector3D pos, Vector3D rot )
	{
		throw new UnsupportedOperationException();
	}
}
