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

package net.gtaun.shoebill;

import java.util.Vector;

import net.gtaun.lungfish.data.Point;
import net.gtaun.lungfish.data.PointRot;
import net.gtaun.shoebill.streamer.IStreamObject;
import net.gtaun.shoebill.streamer.Streamer;

/**
 * @author MK124
 *
 */

public class DynamicObject extends ObjectBase implements IStreamObject
{
	static final int DEFAULT_RANGE =		300;
	
	
	static Streamer<DynamicObject> streamer;

	public static void initialize( Gamemode gamemode )
	{
		if( streamer == null )
			streamer = new Streamer<DynamicObject>(gamemode, DEFAULT_RANGE);
	}
	public static void initialize( Gamemode gamemode, int range )
	{
		if( streamer == null )
			streamer = new Streamer<DynamicObject>(gamemode, range);
	}
	
	@Deprecated
	public static <T> Vector<T> get( Class<T> cls )
	{
		return null;
	}
	
	
	int id[] = new int[ Gamemode.MAX_PLAYERS ];
	
	
	public DynamicObject( int model, float x, float y, float z, float rx, float ry, float rz )
	{
		this.model = model;
		this.position = new PointRot( x, y, z, rx, ry, rz );
		
		init();
	}
	
	public DynamicObject( int model, Point point, float rx, float ry, float rz )
	{
		this.model = model;
		this.position = new PointRot( point, rx, ry, rz );
		
		init();
	}
	
	public DynamicObject( int model, PointRot point )
	{
		this.model = model;
		this.position = point.clone();
		
		init();
	}
	
	private void init()
	{	
		for( int i = 0; i < id.length; i++ ) id[i] = -1;
		streamer.add( this );
	}


//---------------------------------------------------------
	
	public int onMoved()
	{
		return 1;
	}
	

//---------------------------------------------------------

	public void destroy()
	{
		streamer.remove( this );
		
		for( int i=0; i<500; i++ )
			NativeFunction.destroyPlayerObject( i, id[i] );
	}
	
	public PointRot position()
	{
		return position.clone();
	}
	
	public void setPosition( Point position )
	{
		this.position.set( position );
		for( int i=0; i<500; i++ )
		{
			if( id[i] < 0 ) continue;
			NativeFunction.setPlayerObjectPos( i, id[i], position.x, position.y, position.z );
		}
	}
	
	public void setPosition( PointRot position )
	{
		this.position = position.clone();
		for( int i=0; i<500; i++ )
		{
			if( id[i] < 0 ) continue;
			
			NativeFunction.setPlayerObjectPos( i, id[i], position.x, position.y, position.z );
			NativeFunction.setPlayerObjectRot( i, id[i], position.rx, position.ry, position.rz );
		}
	}
	
	public void setRotate( float rx, float ry, float rz )
	{
		position.rx = rx;
		position.ry = ry;
		position.rz = rz;
		
		for( int i=0; i<Gamemode.MAX_PLAYERS; i++ )
		{
			if( id[i] < 0 ) continue;
			NativeFunction.setPlayerObjectRot( i, id[i], rx, ry, rz );
		}
	}
	
	public int move( float x, float y, float z, float speed )
	{
		return 0;
	}
	
	public void stop()
	{
		
	}
	
	public void attach( Player player, float x, float y, float z, float rx, float ry, float rz )
	{
		
	}

	
//---------------------------------------------------------

	public Point getPosition()
	{
		return position;
	}
	
	public void streamIn( Player player )
	{
		if( id[player.id] == -1 )
		{
			id[player.id] = NativeFunction.createPlayerObject( player.id, model,
					position.x, position.y, position.z, position.rx, position.ry, position.rz, drawDistance );
		}
	}

	public void streamOut( Player player )
	{
		if( id[player.id] != -1 )
		{
			NativeFunction.destroyPlayerObject( player.id, id[player.id] );
			id[player.id] = -1;
		}
	}
}