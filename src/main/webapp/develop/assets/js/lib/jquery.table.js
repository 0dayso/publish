/**
 * This jQuery plugin auto  generate table.
 *
 * This plugin required jQuery 1.7.0+
 *
 * @author lumeiqin and Dennis.li;
 * @version 0.6.0 @2016-06-15  11:00
 *
 * add customEvents option;
 */
;(function($){
	$.Grid  = function ( opts ) {
		this.options = opts;
		$.extend(true, this, {
			container: null,
			/**
			 * columns {array} 期望接收一个数组；数组中的每一项为一个配置对象，规则如下：
			 * {
			 * 		title: {string} 可选配置 表格头部标题；
			 *		field: {string} 可选配置项； 没有提供field 的会动态生成一个唯一的字段名；
			 *		width: {string/number} 可选配置项 默认为auto； width 配置不能为百分比；
			 *				因为有一种情况限制就是百分比和auto 同时出现无法计算宽度占比；
		 	 *		fixed: {boolean} 可选配置项， 当设置为true 的时候该列宽度为固定值；
		 	 *		        1）当所有列中有width: 'auto'的时候，其它配置列的宽度自动固定；
		 	 *		        2）当没有auto 的时候，非fixed 列宽度会动态计算占比；
		 	 *		cls: {string} 可选配置项  添加到单元格的附加样式 ，
		 	 *			注意所有的 margin-left,margin-right,padding-left, padding-right 会被用0覆盖;
		 	 *	    hcls: {string} 可选配置项， 添加到表头的样式，ml,mr,pl,pr 会用0覆盖；
			 * }
			 *
			 */
			columns: null,
			source: false,
			curpage: 1,
			pageSize: 20,
			height: false,
			minHeight: 100,
			/**
			 * fixedRowHeight {boolean}固定表格行高 高度，默认为固定高度；（单行显示）
			 * 设置为false 自动换行，以多行形式显示；
			 */
			fixedRowHeight: true,
			/**
			 *  rowNumber {boolean}
			 *  如果为true 在table 的第一个列添加一个序列数字；
			 */
			rowNumber: false,
			/**
			 *  hasCheckbox {boolean}
			 *  如果为true 在table 每一列前面添加一个复选框；
			 */
			hasCheckbox: false,
			/**
			 *  checkboxValueField {string}
			 *  checkbox 所包含的值；
			 */
			checkboxValueField: '',
			/**
			 * pagination default false;
			 * 该参数如果设置为true ,自动创建分页；
			 * @pagination {boolean}
			 */
			pagination: false,
			/**
			 * proxy { Object } 所有需要请求的链接都放在该对象下面；
			 * @proxy.read  { String }/{ Object } 获取数据的链接；
			 * 	如果配置一个对象，则对象中的配置项同jQuery ajax 配置项；
			 *
			 * 	后面如果有增删该查的所有地址都配置在此处；现在预留一个配置对象，以便后面扩展；
			 */
			proxy: {
				read: ''
			},
			/**
			 * queryParams {string}/{object} / {function} 调用通用ajax 方法添加的额外参数；
			 * 该参数会累加到 请求参数上； 如果是一个Function 则可以动态控制额外参数；该Function 需要返回一个对象；
			 */
			queryParams: {},
			/**
			 * 可以自己配置 分页参数的查询参数;
			 */
			paginationQueryKey: {
				page: 'currentPage',
				size: 'pageSize'
			},
			/**
			 *  提供统一管理的ajax 方法；
			 */
			offerCommonAjax: false,
			/**
			 * loading {boolean/object} 可选配置项，默认在请求数据之前会添加loading;
			 * 设置为false 禁用loading;
			 * 远程加载数据 在发送请求之前 会调用 beforeSend 方法；
			 * 接收到数据之后会调用afterReceived 方法；
			 * delay 创建延迟； 默认 100ms;
			 */
			loading: {
				delay: 100,
				beforeSend: $.noop,
				afterReceived: $.noop
			},
			/**
			 * filterData 所有数据，都要经过filterData 过滤；
			 * 		@param data 原始data数据，需要过滤之前的数据；
			 */
			filterData: function ( data ) { return data; },
			/**
			 *  配置 table 的工具条； 默认无工具条；
			 *  toolbar {array} 数组中的每一项为一个配置对象；
			 *  eg.
			 *  	{
			 *			text: '添加'，
			 *			icon: '',
			 *			cls: '',
			 *			handle: function () {}
			 *     }
			 */
			toolbar: false,
			/**
			 *  Grid 网格header 标题；
			 *  gridHeader {object}
			 *  {
			 *  	title: '用户列表',
			 * 		icon: '',
			 * 		cls: '',
			 *		toolbar: [{
			 *			icon: '',
			 *			handle: function () {}
			 *		}]
			 *  }
			 */
			gridHeader: false,
			/**
			 *  Events {string}
			 *  onLoadSuccess 在Grid 加载完成以后触发；
			 */
			events: {
				onBeforeLoad: $.noop,
				onLoadSuccess: $.noop,
				onRowClick: $.noop,
				onRowDbClick: $.noop
			},
			/**
			 * custom defined events;
			 * customEvents {array、object} optional
			 * 用户自定义事件；
			 * * items  可以是单个需要绑定事件的对象，也可以是一个需要绑定事件的对象数组；
			 *  [{
			 * 		target: '.classname.Table, #idName.Table, ...'，@target 只能为字符串；
			 * 		emit: 'click',@emit  触发事件名称；
			 * 		data: ; // 绑定到触发事件的额外参数；
			 * 		delegate: null,@delegate 使用委托绑定需要提供委托绑定对象；
			 *  }]
			 */
			customEvents: false
		}, opts);

		this.init();
	};

	var privateMgr = {
			tpls: {
				icon: '<a href="javascript:void(0);"><i class="{icon}"></i></a>',
				toolbar: '<a href="{href}" class="btn {className} {cls}">\
								<i class="{icon}"></i>\
								<span class="" >{text}</span>\
							</a>',
				gridHeader: '<div class="fl-l {cls}">\
								<i class="{icon}"></i>\
								<span class="fw-b">{title}</span>\
							</div>\
							<div class="grid-header-toolbar fl-r">{toolIcon}</div>',
				tableTr: '<tr data-rowData="{rowNum}" class="{rowStrip} {gridRowCls}">{tdHtml}</tr>',
				tableTd: '<td data-field="{field}" class="{gridCellCls}" ><div class="{cls} {fieldCls}">{text}</div></td>',
				rowNumber: '<td class="{numCls}"><div>{serialNum}</div></td>',
				hasCheckbox: '<td class="{checkboxCellCls}"><div><input type="checkbox" class="{checkboxCls}" name="{name}" value="{valueField}" /></div></td>'
			},
		/**
		 *
		 * @param str {string} 要渲染的模板字符串；
		 * @param data {object} 渲染到模板的数据，只能是扁平化的对象，对象中可以包含数组，但是不能嵌套对象；
		 * @returns {string|void|XML}
         */
			renderTpl: function ( str, data ) {
				var indexMap = {};

				return str.replace(/\{[^\}]*\}/g, function ( m ) {
						var key = $.trim(m.slice(1,-1)),
							renderData = data[key];

						if ($.isArray(renderData)) {
							// 下面的判断会过滤掉 undefined 和 null;
							indexMap[key] == undefined ? (indexMap[key] = 0) : (indexMap[key] += 1);

							return renderData[indexMap[key]];
						} else {
							return renderData == undefined ?  '' : renderData;
						}
					});
			}
		};
	$.extend($.Grid.prototype, {
		init: function () {
			var me = this;

			/**
			 * defaultCls / layoutSize 这里两个配置项内部使用，尽量不要修改他们；
             */
			me.defaultCls = {
				numCls: 'row-number-cell',
				checkboxCellCls: 'checkbox-cell',
				checkboxCls: 'grid-checkbox',
				selectId: 'row-selectAll' + (+new Date).toString(36),
				gridCellCls: 'grid-cell',
				gridCellPreCls: 'grid-td-' + (+new Date).toString(36),
				gridRowCls: 'grid-row'
			};
			me.layoutSize = {
				/** 容器的总宽度 */
				containerWidth: 0,
				/** 固定字段的总长度 */
				fixedWidth: 0,
				/** 数组中的每项是一个对象 对象包含一个field 一个width 值；*/
				dynamicFields: [],
				/** 动态宽度 总长度 */
				dynamicWidth: 0,
				/** rate: (containerWidth - fixed) / dynamicWidth */
				rate: 1
			};
			me.container.addClass('jq-grid');
			me.initGridHeader();
			me.initToolbar();
			me.initTableHeader();
			me.reload( me.source );
			me.bindEvents();
			me.customEvents && me.addListeners(me.customEvents);
		},
		initGridHeader: function () {
			var me = this,
				eventGroup = [],
				iconArr = [],
				toolIconHtml = '',
				i = 0, len;

			if ( !me.gridHeader ) {
				return false;
			}

			// 首先组合里面的工具按钮，如果有；
			if ( $.isArray(me.gridHeader.toolbar) ) {
				for (len = me.gridHeader.toolbar.length; i < len; ++i) {
					toolIconHtml += privateMgr.tpls.icon;
					iconArr.push(me.gridHeader.toolbar[i].icon + ' -grid-header-toolbar' + i);
					eventGroup.push({
						target: '.-grid-header-toolbar' + i,
						emit: 'click.Table',
						handle: me.gridHeader.toolbar[i].handle || $.noop,
						delegate: me.container
					});
				}
			}

			if ( !me.gridHeaderConatiner ) {
				me.gridHeaderConatiner = $('<div class="cf grid-header"></div>');
				me.container.append(me.gridHeaderConatiner);
			}

			me.gridHeaderConatiner.html(privateMgr.renderTpl(privateMgr.tpls.gridHeader,{
				cls: me.gridHeader.cls,
				title: me.gridHeader.title,
				icon: me.gridHeader.icon,
				toolIcon: privateMgr.renderTpl(toolIconHtml,{ icon: iconArr })
			}));

			me.addListeners(eventGroup);

			return true;
		},
		initToolbar: function () {
			var me = this,
				eventGroup = [],
				html = '',
				i = 0, len;

			if ( !$.isArray(me.toolbar) ) {
				return false;
			}

			for (len = me.toolbar.length; i < len; ++i) {
				html += privateMgr.renderTpl(privateMgr.tpls.toolbar, {
						text: me.toolbar[i].text,
						icon: me.toolbar[i].icon,
						cls: me.toolbar[i].cls,
						className: '-toolbar-btn' + i,
						href: me.toolbar[i].href || 'javascript:void(0);'
					});
				eventGroup.push({
					target: '.-toolbar-btn' + i,
					emit: 'click.Table',
					handle: me.toolbar[i].handle || $.noop,
					data: me,
					delegate: me.container
				});
			}

			me.container.append('<div class="toolbar-container">' + html + '</div>');
			me.addListeners(eventGroup);

			return true;
		},
		initTableHeader: function () {
			// 创建 table 容器； Header 容器， body 容器；
			var cssText = '';

			cssText += this.height ? +this.height ?  ('height:' + this.height + 'px;') : ('height:' + this.height + ';') : '';
			cssText += this.minHeight ? +this.minHeight ?  ('min-height:' + this.minHeight + 'px;') : ('min-height:' + this.minHeight + ';') : '';


			this.tableContainer = $('<div class="jq-table"></div>');
			this.tableContainer.append('<div class="grid-table-header">' + this.generateHeaderHtml() + '</div>');
			this.tableBodyContainer = $('<div class="grid-table-body"'+ (cssText ? 'style="'+ cssText + '"' : "") + '></div>');
			this.tableContainer.append(this.tableBodyContainer);
			this.container.append(this.tableContainer);
		},
		/**
		 * items  可以是单个需要绑定事件的对象，也可以是一个需要绑定事件的对象数组；
		 * {
		 * 		@target 只能为字符串；
		 * 		target: '.classname.Table, #idName.Table, ...'，
		 *		@emit  触发事件名称；
		 * 		emit: 'click',
		 * 		@delegate 使用委托绑定需要提供委托绑定对象；
		 * 		delegate: null
		 * }
		 * @param items
         */
		addListeners: function (items) {
			var me = this,
				i = 0, len,
				tempElem;

			!$.isArray(items) && (items = [items]);

			for (len = items.length; i < len; ++i) {
				if ( items[i].delegate ) {
					tempElem = me.getJQElem(items[i].delegate);

					/** 对于传递了delegate 值的绑定事件，如果获取不到该值则忽略该事件绑定； */
					if ( tempElem.length ) {
						tempElem.on(items[i].emit, items[i].target, items[i].data || me, items[i].handle);
					}
				} else {
					/** 如果没有传递delegate 绑定事件的，使用on 方法的bing功能 绑定 事件*/
					tempElem = me.getJQElem(items[i].target);
					tempElem.length && tempElem.on(items[i].emit, items[i].data || me, items[i].handle);
				}
			}
		},
		indexOfElem: function (arr, elem) {
			var ret = -1;

			$.each(arr, function (i) {
				if ( this[0] === elem[0] ) {
					ret = i;
					return false;
				}
			});

			return ret;
		},
		getJQElem: function ( d ) {
			var elem = this.container.find(d);
			!elem.length && (elem = $(d));
			return elem;
		},
		getData: function () {
			return this.source.data || [];
		},
		getSelectedData: function () {
			var hasSelected = this.container.find('tr.selected').attr('data-rowData');
			if ( $.isNumeric(hasSelected) ) {
				return this.getData()[hasSelected];
			}

			return undefined;
		},
		getCurrentPageIndex: function () {
			return this.curpage;
		},
		/**
		 * generateHeaderHtml 生成头部html 字符串；
		 * @renderData {object}
		 * 	.numCls   		数字序列的样式；	 可在CSS 中修改
		 * 	.checkboxCls  	复选框单元格样式；可在CSS 中修改
		 * 	.selectId		动态生成的复选框ID; 用于选中多行使用；
		 * 	.gridCellPreCls    单元格前缀样式；    用于统一改变单元格的宽度；
		 *	.hcls			头部单元格样式；    用户配置参数，在columns 中；
		 *
		 * @returns {string} html
		 */
		generateHeaderHtml:function(){
			var me = this,
				columns = me.columns,
				renderClsData = $.extend({},me.defaultCls),
				html = '',styleText = '',field,
				noAuto = true, i = 0,len;

			if( !$.isArray (columns) ) {
				return '';
			}

			html += '<table><thead><tr>';
			if ( me.rowNumber ) {
				html += '<th class="'+ renderClsData.numCls +'"><div style="width: 30px" ></div></th>';
				me.layoutSize.fixedWidth += 31;
			}
			if ( me.hasCheckbox ) {
				html += '<th class="'+ renderClsData.checkboxCellCls +'"><div style="width: 30px" ><input type="checkbox" id="'+ renderClsData.selectId +'" /></div></th>';
				me.layoutSize.fixedWidth += 31;
			}

			for( len = columns.length; i < len; i++){
				field = columns[i].field || (columns[i].field = (+new Date).toString(36) + i);
				html += '<th data-field="'+ field +'" ><div class="'+ renderClsData.gridCellPreCls + field + (i == len - 1 ? ' grid-header-last ' : ' ') +
					(columns[i].hcls || "") + '">'+ (columns[i].title || '')+'</div></th>';


				// 使用隐式转换 0 和其它任何不能转换成整数的值忽略掉；如果你一定要填负数的话，那只能抱歉的说你随意；
				if ( columns[i].fixed && +columns[i].width ) {
					me.layoutSize.fixedWidth += +columns[i].width;
					/** 添加宽度固定字段样式； */
					styleText += me.generateFieldStyle (field, columns[i].width);
				} else {
					me.layoutSize.dynamicFields.push({
						// IE 8 不支持动态建 { [field]: width } 所以使用两个键；
						field : field,
						width: columns[i].width || 'auto'
					});

					// 如果配置宽度为数字则取该数字，否则设置hasAuto 为0
					me.layoutSize.dynamicWidth += +columns[i].width ? +columns[i].width : (noAuto = 0);
				}
			}

			if ( noAuto === 0 ) {
				// 过滤掉dynamicFields 中的非 auto 项；并把非auto 项添加到fixed 样式表中；
				me.layoutSize.dynamicFields = $.grep(me.layoutSize.dynamicFields, function (n, i) {
					if ( n.width === 'auto' ) {
						return true;
					} else {
						styleText += me.generateFieldStyle (n.field, n.width);
					}

					return false;
				});
			}

			me.tableContainer.prepend('<style class="fixed-style" type="text/css">' + styleText + '</style>');
			//me.tableDynamicStyle = $('<style class="dynamic-style" type="text/css"></style>');
			//me.tableContainer.prepend(me.tableDynamicStyle);
			html += '</tr></thead></table>';
			me.resize();

			return html;
		},
		resizeLayoutSize: function ( hasAuto ) {
			var me = this;

			if ( me.tableBodyContainer ) {
				me.layoutSize.containerWidth = me.tableBodyContainer[0].clientWidth;
			} else if ( me.gridHeaderConatiner ) {
				me.layoutSize.containerWidth = me.gridHeaderConatiner[0].clientWidth;
			} else {
				return false;
			}

			!hasAuto && $.each(me.layoutSize.dynamicFields, function (i, n) {
				hasAuto = n.width === 'auto';
				return !hasAuto;
			});

			if ( hasAuto ) {
				me.layoutSize.fixedWidth += me.layoutSize.dynamicWidth;
				me.layoutSize.dynamicWidth = 0;
				me.layoutSize.rate = Math.round((me.layoutSize.containerWidth - me.layoutSize.fixedWidth) / me.layoutSize.dynamicFields.length);
			} else {
				me.layoutSize.rate = (me.layoutSize.containerWidth - me.layoutSize.fixedWidth) / me.layoutSize.dynamicWidth;
			}
		},
		/**
		 *  generateListHtml 生成数据表格中行 的html 字符串；
		 *
		 *  @return {string} html
		 */
		generateListHtml: function () {
			var me = this,
				source = me.source,
				list = source.data,
				html = '<table><tbody>',
				i = 0, len;

			if( $.isArray(list) && list.length ){
				len = list.length;

				for ( ; i < len; ++i ) {
					html += me.generateTr( list[i], i);
				}
			}else{
				html = '<table width="100%"><tbody><tr><td class="'+ me.defaultCls.gridCellCls +'" align="center">暂无数据</td></tr>';
			}

			html += '</tbody></table>';

			return html;
		},
		generateTable: function () {
			var me = this;

			me.tableBodyContainer.html(me.generateListHtml());
			if ( me.pagination ) {
				me.generatePagination();
			}
		},
		generatePagination:function(){
			var me = this,
				source = me.source;

			if( $.isArray( source.data )  && source.data.length){
				if ( !me.paginationContainer ) {
					me.paginationContainer = $('<div class="grid-pagination cf" style="margin-top: 10px"></div>');
					me.container.append(me.paginationContainer);
				}

				me.paginationContainer.pagination( source.totalCount, {
						items_per_page: me.pageSize,
						num_display_entries: 8,
						current_page: me.curpage - 1,
						num_edge_entries: 1,
						prev_text:"上一页",
						next_text:"下一页",
						jump_page: true,
						callback: function(pageIndex){
							me.curpage = pageIndex;
							me.reload();
						}
				});
			}
		},
		generateTr: function(rowData, rowNum){
			var me = this,
				columns = me.columns,
				tdHtml = '',
				renderData = $.extend(true,{},me.defaultCls,{
					rowNum: rowNum,
					serialNum: rowNum + 1,
					rowStrip: rowNum % 2 ? "even" : "odd",
					valueField: rowData[me.checkboxValueField],
					name: me.checkboxValueField
				}),
				tempVal, field, i = 0;

			me.rowNumber && (tdHtml += privateMgr.renderTpl(privateMgr.tpls.rowNumber, renderData));
			me.hasCheckbox && (tdHtml += privateMgr.renderTpl(privateMgr.tpls.hasCheckbox, renderData));

			for(; i< columns.length; i++){
				field = columns[i].field;
				tempVal = (columns[i].formatter || $.noop)(rowData[field], rowData);

				tdHtml += privateMgr.renderTpl(privateMgr.tpls.tableTd, {
					gridCellCls: me.defaultCls.gridCellCls,
					fieldCls: me.defaultCls.gridCellPreCls + (field || '') +( me.fixedRowHeight ?  ' fixed-height' : ' ww-bw'),
					cls: columns[i].cls,
					text: tempVal == undefined ? rowData[field] : tempVal,
					field: field
				});
			}
			renderData.tdHtml = tdHtml;

			return privateMgr.renderTpl(privateMgr.tpls.tableTr, renderData);
		},
		generateFieldStyle: function ( field, width ) {
			return '.jq-table .' + this.defaultCls.gridCellPreCls  + field +
						'{width:' + ( +width ? (width + 'px') : width) +
					';margin-left:0;margin-right:0;padding-left:0;padding-right:0;}';
		},
		bindEvents:function(){
			var me = this;

			$(window).on('resize.Table', me, function () {
				if ( !me.container.resizeTable ) {
					me.container.resizeTable = true;
					setTimeout(function () {
						me.resize();
						me.container.resizeTable = false;
					}, 100);
				}
			});

			/** 绑定事件 */
			me.addListeners([{
				target: '#' + me.defaultCls.selectId,
				emit: 'click.Table',
				delegate: me.container,
				data: [me, me.defaultCls.checkboxCls],
				handle: function ( event ) {
					var me = event.data[0];

					me.container.find('.' + event.data[1]).prop("checked", $(this).prop("checked"));
				}
			},{
				target: '.' + me.defaultCls.checkboxCls,
				emit: 'click.Table',
				delegate: me.container,
				data: [me, me.defaultCls.selectId, me.defaultCls.checkboxCls],
				handle: function ( event ) {
					var me = event.data[0];
					var trCheckboxs = me.container.find('.' + event.data[2]);

					me.container.find('#' + event.data[1])
						.prop('checked',trCheckboxs.length === trCheckboxs.filter(':checked').length);
				}
			},{
				target: '.' + me.defaultCls.gridRowCls,
				emit: 'click.Table',
				delegate: me.container,
				data: [me, me.defaultCls.gridRowCls],
				handle: function (e) {
					$(this).addClass('selected')
						.siblings('.' + e.data[1]).removeClass('selected');
					me.events.onRowClick.call(this, e);
				}
			},{
				target: '.' + me.defaultCls.gridRowCls,
				emit: 'dblclick.Table',
				delegate: me.container,
				data: [me, me.defaultCls.gridRowCls],
				handle: function (e) {
					me.events.onRowDbClick.call(this, e);
				}
			}]);
		},
		/**
		 * @private
		 * 根据opts获取远程数据；
		 * 请求完成调用 cb ；
		 * @param cb
         */
		getRemoteData: function ( cb ) {
			var me = this;
			var data = {};
			var readOpts = typeof me.proxy.read === 'string' ? { url: me.proxy.read } :  me.proxy.read;

			if ( $.isFunction( me.queryParams ) ) {
				$.extend(data, me.queryParams());
			} else if ( $.isPlainObject( me.queryParams ) ) {
				$.extend(data, me.queryParams);
			}

			/** 获取 当前table 的当前页 和配置的每页的数量 */
			if ( me.pagination ) {
				data[me.paginationQueryKey.page] = me.curpage;
				data[me.paginationQueryKey.size] = me.pageSize;
			}

			/** 如果queryParams 是字符串 */
			typeof me.queryParams === 'string' &&
			(data = me.queryParams + '&' + $.param(data));

			me.offerCommonAjax( $.extend(true,{}, readOpts, {
				data: data,
				dataType: 'json',
				complete: function ( xhr ) {
					var json = xhr.responseJSON;

					json = json || {};
					$.isFunction( cb ) && cb( json );
					if ( $.isFunction(readOpts.complete) ) {
						return readOpts.complete.apply(this, arguments);
					}
				}
			}) );
		},
		/**
		 * reload 根据配置项重新填充数据；
		 * 该方法会自动获取当前分页插件中当前页，然后发送请求；
		 * @param data  {object} 要加载的数据源，如果提供了data,则加载data 数据，
		 * 否则先从远程加载，再判断本地数据加载；
		 */
		reload: function ( data ) {
			var me = this,
				_reload = function ( d ) {
					me.source = me.filterData( d );
					me.events.onBeforeLoad.call(me);
					me.generateTable();
					me.resize();
					/** 生成table 完成之后 调用 用户配置的onLoadSuccess;  */
					me.events.onLoadSuccess.call(me, me.source);
					clearTimeout(me.loadingTimer);
					!me.loadingTimer && me.loading.afterReceived.call(me);
					me.lockGridGenerate = false;
				};

			if ( !me.lockGridGenerate ) {
				me.lockGridGenerate = true;
			} else  {
				return false;
			}

			if (data) {
				_reload( data );
			} else if ( $.isFunction(me.offerCommonAjax) ) {
				me.loading && (me.loadingTimer = setTimeout(function () {
									me.loading.beforeSend.call(me);
									me.loadingTimer = undefined;
								}, me.loading.delay));

				me.getRemoteData(_reload);
			} else {
				_reload( me.source );
			}
		},
		getCheckedSerialValues: function () {
			return this.container.find('.'+ this.defaultCls.checkboxCls + ':checked').serialize();
		},
		getCheckedValues: function () {
			var ret = [];

			this.container
				.find('.'+ this.defaultCls.checkboxCls + ':checked')
				.each(function () {
					ret.push(this.value);
				});

			return ret;
		},
		resize: function() {
			var me = this,
				width,
				styleText = '';

			/**
			 * 过滤多余的宽度调整；
			 * 如果 me.layoutSize.containerWidth 有值，且=== me.gridHeaderContainerWidth 则不执行调整；
			 */
			if ( me.tableBodyContainer && me.tableBodyContainer[0].clientWidth === me.layoutSize.containerWidth ) {
				return false;
			}

			me.resizeLayoutSize();

			$.each(me.layoutSize.dynamicFields, function(i, n) {
				width = Math.floor((+n.width ? n.width * me.layoutSize.rate : 1 * me.layoutSize.rate)*10) / 10 - 1;
				styleText += me.generateFieldStyle(n.field, width);
			});

			/** compatible IE 8  IE8 下面style 样式不能被动态创建和动态改变内容 */
			me.tableContainer.find('style.dynamic-style').remove();
			me.tableContainer.prepend('<style class="dynamic-style" type="text/css">'+ styleText +'</style>');
		},
		destroy: function () {
			this.container.off('.Table');
			this.container.removeClass('jq-grid').html('');
		},
		toggleRowHeight: function ( row ) {
			row.find('.'+ this.defaultCls.gridCellCls + '>div').toggleClass('fixed-height');
		},
		setRowHeightFixed: function ( row ) {
			row.find('.'+ this.defaultCls.gridCellCls + '>div').addClass('fixed-height');
		},
		setRowHeightAuto: function ( row ) {
			row.find('.'+ this.defaultCls.gridCellCls + '>div').removeClass('fixed-height');
		}
	});

	$.fn.jtable = function( options ){
		options.container = this;
		return new $.Grid(options);
	};
})(jQuery);
