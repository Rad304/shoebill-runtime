/**
 * Copyright (C) 2011 MK124
 * Copyright (C) 2011 JoJLlmAn
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
import net.gtaun.shoebill.data.Point;
import net.gtaun.shoebill.data.PointRot;
import net.gtaun.shoebill.samp.SampNativeFunction;
import net.gtaun.shoebill.util.event.EventDispatcher;
import net.gtaun.shoebill.util.event.IEventDispatcher;

/**
 * @author MK124, JoJLlmAn
 *
 */

public class ObjectBase implements IDestroyable
{
	public static final int INVALID_ID =				0xFFFF;
	
	
	public static Collection<ObjectBase> get()
	{
		return Shoebill.getInstance().getManagedObjectPool().getObjects();
	}
	
	public static <T extends ObjectBase> Collection<T> get( Class<T> cls )
	{
		return Shoebill.getInstance().getManagedObjectPool().getObjects( cls );
	}
	
	
	EventDispatcher eventDispatcher = new EventDispatcher();
	
	int id = -1;
	int model;
	PointRot position;
	float speed = 0;
	Player attachedPlayer;
	Vehicle attachedVehicle;
	float drawDistance = 0;
	
	
	public IEventDispatcher getEventDispatcher()		{ return eventDispatcher; }
	
	public int getModel()								{ return model; }
	public float getSpeed()								{ return speed; }
	public Player getAttachedPlayer()					{ return attachedPlayer; }
	public Vehicle getAttachedVehicle()					{ return attachedVehicle; }
	public float getDrawDistance()						{ return drawDistance; }
	
	
	ObjectBase()
	{
		
	}
	
	public ObjectBase( int model, float x, float y, float z, float rx, float ry, float rz )
	{
		this.model = model;
		this.position = new PointRot( x, y, z, rx, ry, rz );
		
		init();
	}
	
	public ObjectBase( int model, float x, float y, float z, float rx, float ry, float rz, float drawDistance )
	{
		this.model = model;
		this.position = new PointRot( x, y, z, rx, ry, rz );
		this.drawDistance = drawDistance;
		
		init();
	}
	
	public ObjectBase( int model, Point point, float rx, float ry, float rz )
	{
		this.model = model;
		this.position = new PointRot( point, rx, ry, rz );
		
		init();
	}
	
	public ObjectBase( int model, Point point, float rx, float ry, float rz, float drawDistance)
	{
		this.model = model;
		this.position = new PointRot( point, rx, ry, rz );
		this.drawDistance = drawDistance;
		
		init();
	}
	
	public ObjectBase( int model, PointRot point )
	{
		this.model = model;
		this.position = point.clone();
		
		init();
	}
	
	public ObjectBase( int model, PointRot point, float drawDistance )
	{
		this.model = model;
		this.position = point.clone();
		this.drawDistance = drawDistance;
		
		init();
	}
	
	private void init()
	{
		id = SampNativeFunction.createObject( model, position.x, position.y, position.z, position.rx, position.ry, position.rz, drawDistance );
		
		SampObjectPool pool = (SampObjectPool) Shoebill.getInstance().getManagedObjectPool();
		pool.setObject( id, this );
	}
	

//---------------------------------------------------------

	@Override
	public void destroy()
	{
		SampNativeFunction.destroyObject( id );

		SampObjectPool pool = (SampObjectPool) Shoebill.getInstance().getManagedObjectPool();
		pool.setObject( id, null );
		
		id = -1;
	}
	
	@Override
	public boolean isDestroyed()
	{
		return id == -1;
	}
	
	public PointRot getPosition()
	{
		SampNativeFunction.getObjectPos( id, position );
		SampNativeFunction.getObjectRot( id, position );
		return position.clone();
	}
	
	public void setPosition( Point position )
	{
		this.position.set( position );
		SampNativeFunction.setObjectPos( id, position.x, position.y, position.z );
	}
	
	public void setPosition( PointRot position )
	{
		this.position = position.clone();
		SampNativeFunction.setObjectPos( id, position.x, position.y, position.z );
		SampNativeFunction.setObjectRot( id, position.rx, position.ry, position.rz );
	}
	
	public void setRotate( float rx, float ry, float rz )
	{
		position.rx = rx;
		position.ry = ry;
		position.rz = rz;
		
		SampNativeFunction.setObjectRot( id, rx, ry, rz );
	}
	
	public int move( float x, float y, float z, float speed )
	{
		if(attachedPlayer == null && attachedVehicle == null) this.speed = speed;
		return SampNativeFunction.moveObject( id, x, y, z, speed );
	}
	
	public void stop()
	{
		speed = 0;
		SampNativeFunction.stopObject( id );
	}
	
	public void attach( Player player, float x, float y, float z, float rx, float ry, float rz )
	{
		SampNativeFunction.attachObjectToPlayer( id, player.id, x, y, z, rx, ry, rz );
		attachedPlayer = player;
		speed = 0;
	}
	
	public void attach( Vehicle vehicle, float x, float y, float z, float rx, float ry, float rz )
	{
		SampNativeFunction.attachObjectToVehicle( id, vehicle.id, x, y, z, rx, ry, rz );
		attachedVehicle = vehicle;
		speed = 0;
	}
}
